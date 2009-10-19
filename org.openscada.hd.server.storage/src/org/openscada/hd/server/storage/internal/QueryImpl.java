package org.openscada.hd.server.storage.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.server.storage.ShiService;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.utils.ValueArrayNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the internal implementation of the HD query interface.
 * The class works in direct combination with the class ShiService.
 * Therefore synchronization is done via the related service object.
 * @see org.openscada.hd.Query
 * @see org.openscada.hd.server.storage.ShiService
 * @author Ludwig Straub
 */
public class QueryImpl implements Query, ExtendedStorageChannel, Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    /** Time span between two consecutive calls of the future task. */
    private final static long DELAY_BETWEEN_TWO_QUERY_CALCULATIONS = 500;

    /** Service that created the query object. */
    private final ShiService service;

    /** Listener that should receive the data. */
    private final QueryListener listener;

    /** Set of available calculation methods. */
    private final Set<CalculationMethod> calculationMethods;

    /*** Flag indicating whether the query is registered at the service or not. */
    private final boolean queryRegistered;

    /** Input parameters of the query. */
    private QueryParameters parameters;

    /** Currently set query state. */
    private QueryState currentQueryState;

    /** Flag indicating whether the query is closed or not. */
    private boolean closed;

    /** Task that will calculate the result. */
    private ScheduledExecutorService queryTask;

    /** Maximum availavle compression level. */
    private final long maximumCompressionLevel;

    /** Parameters that were last time sent via the listener. */
    private QueryParameters lastParameters;

    /** Array of value information that was last time sent via the listener. */
    private ValueInformation[] lastValueInformations;

    /** Set of calculation methods that were last time sent via the listener. */
    private Map<String, Value[]> lastData;

    /**
     * Constructor.
     * @param service service that created the query object
     * @param listener listener that should receive the data
     * @param parameters input parameters of the query
     * @param calculationMethods set of calculation methods that will be available via the service
     * @param updateData flag indicating whether the result should be periodically updated or not
     */
    public QueryImpl ( final ShiService service, final QueryListener listener, final QueryParameters parameters, final Set<CalculationMethod> calculationMethods, final boolean updateData )
    {
        this.service = service;
        this.listener = listener;
        this.parameters = parameters;
        this.calculationMethods = Collections.unmodifiableSet ( calculationMethods );
        lastParameters = null;
        lastValueInformations = null;
        lastData = null;
        maximumCompressionLevel = service.getMaximumCompressionLevel ();
        this.closed = ( service == null ) || ( listener == null ) || ( parameters == null ) || ( parameters.getStartTimestamp () == null ) || ( parameters.getEndTimestamp () == null ) || ( parameters.getEntries () < 1 ) || calculationMethods.isEmpty ();
        if ( closed )
        {
            logger.error ( "not all data is available to execute query. no action will be performed" );
            setQueryState ( QueryState.DISCONNECTED );
            queryRegistered = false;
        }
        else
        {
            setQueryState ( QueryState.LOADING );
            queryTask = new ScheduledThreadPoolExecutor ( 1 );
            if ( updateData )
            {
                service.addQuery ( this );
                queryRegistered = true;
                queryTask.scheduleWithFixedDelay ( this, 0, DELAY_BETWEEN_TWO_QUERY_CALCULATIONS, TimeUnit.MILLISECONDS );
            }
            else
            {
                queryRegistered = false;
                queryTask.schedule ( this, 0, TimeUnit.MILLISECONDS );
            }
        }
    }

    /**
     * This method updates the query state if the new state differs from the current state.
     * @param queryState new query state
     */
    private void setQueryState ( QueryState queryState )
    {
        if ( ( listener != null ) && ( currentQueryState == null ) || ( currentQueryState != queryState ) )
        {
            currentQueryState = queryState;
            listener.updateState ( queryState );
        }
    }

    /**
     * This method removes element from the end of the second array until there is no more time overlapping between the first and the second array.
     * The method returns the shortened second array.
     * @param firstArray first array
     * @param secondArray second array
     * @return shortened second array
     */
    private static BaseValue[] removeTimeOverlay ( final BaseValue[] firstArray, final BaseValue[] secondArray )
    {
        if ( ( firstArray.length == 0 ) || ( secondArray.length == 0 ) )
        {
            return secondArray;
        }
        final long timeBorderValue = firstArray[0].getTime ();
        for ( int i = secondArray.length - 1; i >= 0; i-- )
        {
            if ( secondArray[i].getTime () < timeBorderValue )
            {
                final BaseValue[] resultArray = secondArray instanceof LongValue[] ? new LongValue[i + 1] : new DoubleValue[i + 1];
                for ( int j = 0; j <= i; j++ )
                {
                    resultArray[j] = secondArray[j];
                }
                return resultArray;
            }
        }
        return secondArray;
    }

    /**
     * This method merges the two passed arrays. The second array will be joined before the first array.
     * The result array has the same type as the second array.
     * If the types do not match, an automatic conversion is performed.
     * @param firstArray first array that has to be merged. this array can be null
     * @param secondArray second array that has to be merged. this array must not be null
     * @return new array containing the elements of both arrays
     */
    private static BaseValue[] joinValueArrays ( final BaseValue[] firstArray, final BaseValue[] secondArray )
    {
        if ( ( firstArray == null ) || ( firstArray.length == 0 ) )
        {
            return secondArray;
        }
        if ( secondArray instanceof LongValue[] )
        {
            final LongValue[] secondArray1 = (LongValue[])firstArray;
            final LongValue[] result = new LongValue[firstArray.length + secondArray.length];
            for ( int i = 0; i < secondArray.length; i++ )
            {
                result[i] = secondArray1[i];
            }
            if ( firstArray instanceof LongValue[] )
            {
                final LongValue[] firstArray1 = (LongValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    result[j] = firstArray1[i];
                }
            }
            else
            {
                final DoubleValue[] firstArray1 = (DoubleValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    final DoubleValue srcValue = firstArray1[i];
                    result[j] = new LongValue ( srcValue.getTime (), srcValue.getQualityIndicator (), srcValue.getBaseValueCount (), (long)srcValue.getValue () );
                }
            }
            return result;
        }
        else
        {
            final DoubleValue[] secondArray1 = (DoubleValue[])secondArray;
            final DoubleValue[] result = new DoubleValue[firstArray.length + secondArray.length];
            for ( int i = 0; i < secondArray.length; i++ )
            {
                result[i] = secondArray1[i];
            }
            if ( firstArray instanceof DoubleValue[] )
            {
                final DoubleValue[] firstArray1 = (DoubleValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    result[j] = firstArray1[i];
                }
            }
            else
            {
                final LongValue[] firstArray1 = (LongValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    final LongValue srcValue = firstArray1[i];
                    result[j] = new DoubleValue ( srcValue.getTime (), srcValue.getQualityIndicator (), srcValue.getBaseValueCount (), srcValue.getValue () );
                }
            }
            return result;
        }
    }

    /**
     * This method processes the query.
     */
    public void run ()
    {
        try
        {
            // prepare all data that is required for calculation
            QueryParameters parameters = null;
            long maximumCompressionLevel = 0;
            long startTime = 0;
            long endTime = 0;
            int resultSize = 0;
            Map<StorageChannelMetaData, BaseValue[]> mergeMap = null;
            synchronized ( service )
            {
                // get values that won't change during the calculation
                parameters = this.parameters;
                maximumCompressionLevel = this.maximumCompressionLevel;
                startTime = parameters.getStartTimestamp ().getTimeInMillis ();
                endTime = parameters.getEndTimestamp ().getTimeInMillis ();
                resultSize = parameters.getEntries ();

                // load raw data that has to be normalized later
                long currentCompressionLevel = 0;
                long oldestValueTime = Long.MAX_VALUE;
                while ( ( oldestValueTime > startTime ) && ( currentCompressionLevel <= maximumCompressionLevel ) )
                {
                    if ( currentCompressionLevel == 0 )
                    {
                        mergeMap = service.getValues ( currentCompressionLevel, startTime, endTime );
                        for ( final Entry<StorageChannelMetaData, BaseValue[]> mergeEntry : mergeMap.entrySet () )
                        {
                            final DataType dataType = mergeEntry.getKey ().getDataType ();
                            final BaseValue[] mergeValues = mergeEntry.getValue ();
                            final long now = System.currentTimeMillis ();
                            final long maxTime = mergeValues.length > 0 ? Math.max ( mergeValues[mergeValues.length - 1].getTime () + 1, now ) : now;
                            if ( dataType == DataType.LONG_VALUE )
                            {
                                final LongValue longValue = new LongValue ( maxTime, 0, 0, 0 );
                                mergeEntry.setValue ( joinValueArrays ( new LongValue[] { longValue }, mergeEntry.getValue () ) );
                            }
                            else
                            {
                                final DoubleValue doubleValue = new DoubleValue ( maxTime, 0, 0, 0 );
                                mergeEntry.setValue ( joinValueArrays ( new DoubleValue[] { doubleValue }, mergeEntry.getValue () ) );
                            }
                        }
                    }
                    else
                    {
                        final Map<StorageChannelMetaData, BaseValue[]> subMap = service.getValues ( currentCompressionLevel, startTime, Math.min ( oldestValueTime, endTime ) );
                        for ( final Entry<StorageChannelMetaData, BaseValue[]> subEntry : subMap.entrySet () )
                        {
                            for ( final Entry<StorageChannelMetaData, BaseValue[]> mergeEntry : mergeMap.entrySet () )
                            {
                                if ( mergeEntry.getKey ().getCalculationMethod () == subEntry.getKey ().getCalculationMethod () )
                                {
                                    final BaseValue[] mergeValues = mergeEntry.getValue ();
                                    mergeEntry.setValue ( joinValueArrays ( mergeEntry.getValue (), removeTimeOverlay ( mergeValues, subEntry.getValue () ) ) );
                                }
                            }
                        }
                    }
                    long oldestLocalValueTime = Long.MAX_VALUE;
                    for ( final BaseValue[] baseValues : mergeMap.values () )
                    {
                        if ( baseValues.length > 0 )
                        {
                            oldestLocalValueTime = oldestLocalValueTime == Long.MAX_VALUE ? baseValues[0].getTime () : Math.max ( oldestLocalValueTime, baseValues[0].getTime () );
                        }
                    }
                    oldestValueTime = Math.min ( oldestValueTime, oldestLocalValueTime );
                    currentCompressionLevel++;
                }
            }

            // since all data is collected now, the normalizing can be performed
            final ValueInformation[] resultValueInformations = new ValueInformation[resultSize];
            final Map<String, Value[]> resultMap = new HashMap<String, Value[]> ();
            for ( final CalculationMethod calculationMethod : calculationMethods )
            {
                resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), new Value[0] );
            }
            final long requestedTimeSpan = endTime - startTime;
            final double requestedValueFrequency = (double)requestedTimeSpan / resultSize;
            boolean metaInformationCalculated = false;
            // get raw storage channel data from service
            for ( final Entry<StorageChannelMetaData, BaseValue[]> entry : mergeMap.entrySet () )
            {
                // get current compression level
                final StorageChannelMetaData metaData = entry.getKey ();
                final CalculationLogicProvider calculationLogicProvider = Conversions.getCalculationLogicProvider ( metaData );
                final BaseValue[] values = entry.getValue ();
                final List<BaseValue> resultValues = new ArrayList<BaseValue> ();
                for ( int i = 0; i < resultSize; i++ )
                {
                    final double currentTimeOffsetAsDouble = startTime + i * requestedValueFrequency;
                    final long currentTimeOffsetAsLong = Math.round ( currentTimeOffsetAsDouble );
                    final long localEndTime = Math.round ( currentTimeOffsetAsDouble + requestedValueFrequency );
                    BaseValue[] filledValues = null;
                    if ( values.length == 0 )
                    {
                        if ( values instanceof LongValue[] )
                        {
                            filledValues = new LongValue[] { new LongValue ( currentTimeOffsetAsLong, 0, 0, 0 ), new LongValue ( localEndTime, 0, 0, 0 ) };
                        }
                        else
                        {
                            filledValues = new DoubleValue[] { new DoubleValue ( currentTimeOffsetAsLong, 0, 0, 0 ), new DoubleValue ( localEndTime, 0, 0, 0 ) };
                        }
                    }
                    else
                    {
                        filledValues = ValueArrayNormalizer.extractSubArray ( values, currentTimeOffsetAsLong, localEndTime, 0, values instanceof LongValue[] ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                    }
                    final BaseValue normalizedValue = calculationLogicProvider.generateValues ( filledValues );
                    if ( normalizedValue != null )
                    {
                        resultValues.add ( normalizedValue );
                    }
                }
                final Value[] resultValueArray = new Value[resultSize];
                if ( values instanceof LongValue[] )
                {
                    final LongValue[] longValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY );
                    for ( int i = 0; i < resultValueArray.length; i++ )
                    {
                        final LongValue longValue = longValues[i];
                        resultValueArray[i] = new Value ( longValue.getValue () );
                        if ( !metaInformationCalculated )
                        {
                            final Calendar cstartTime = Calendar.getInstance ();
                            final Calendar cendTime = Calendar.getInstance ();
                            cstartTime.setTimeInMillis ( longValue.getTime () );
                            cendTime.setTimeInMillis ( i == longValues.length - 1 ? endTime : longValues[i + 1].getTime () );
                            resultValueInformations[i] = new ValueInformation ( cstartTime, cendTime, longValue.getQualityIndicator (), longValue.getBaseValueCount () );
                        }
                    }
                }
                else
                {
                    final DoubleValue[] doubleValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                    for ( int i = 0; i < resultValueArray.length; i++ )
                    {
                        final DoubleValue doubleValue = doubleValues[i];
                        resultValueArray[i] = new Value ( doubleValue.getValue () );
                        if ( !metaInformationCalculated )
                        {
                            final Calendar cstartTime = Calendar.getInstance ();
                            final Calendar cendTime = Calendar.getInstance ();
                            cstartTime.setTimeInMillis ( doubleValue.getTime () );
                            cendTime.setTimeInMillis ( i == doubleValues.length - 1 ? endTime : doubleValues[i + 1].getTime () );
                            resultValueInformations[i] = new ValueInformation ( cstartTime, cendTime, doubleValue.getQualityIndicator (), doubleValue.getBaseValueCount () );
                        }
                    }
                }
                metaInformationCalculated = true;
                resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( metaData.getCalculationMethod () ), resultValueArray );
            }

            // send data to listener
            synchronized ( service )
            {
                if ( closed )
                {
                    return;
                }
                sendDataDiff ( parameters, resultMap, resultValueInformations );
            }
        }
        catch ( final Exception e )
        {
            logger.error ( "problem while processing query", e );
            synchronized ( service )
            {
                setQueryState ( QueryState.DISCONNECTED );
                close ();
            }
        }
    }

    /**
     * This method sends the passed data to the listener. If data was sent before, only the data difference is sent.
     * @param parameters parameters that have been used to generate the data
     * @param calculationMethods calculation methods for which data is available
     * @param data data mapped by calculation methods
     * @param valueInformations state information of generated data
     */
    private void sendDataDiff ( final QueryParameters parameters, final Map<String, Value[]> data, final ValueInformation[] valueInformations )
    {
        // do not send any data if input parameters have changed
        if ( !parameters.equals ( this.parameters ) )
        {
            return;
        }

        // compare input parameters of generated data
        final boolean sameParameters = parameters.equals ( lastParameters );
        final Set<String> calculationMethods = new HashSet<String> ( data.keySet () );
        final Set<String> lastCalculationMethods = lastData != null ? lastData.keySet () : null;
        final boolean sameCalculationMethods = ( lastCalculationMethods != null ) && ( calculationMethods.size () == lastCalculationMethods.size () ) && calculationMethods.containsAll ( lastCalculationMethods );

        // prepare sending generated data
        if ( !sameParameters || !sameCalculationMethods )
        {
            listener.updateParameters ( parameters, calculationMethods );
        }

        // compare generated data
        final boolean compatibleValueInformations = ( lastValueInformations != null ) && ( valueInformations.length == lastValueInformations.length );

        // send generated data
        if ( !compatibleValueInformations )
        {
            listener.updateData ( 0, data, valueInformations );
        }
        else
        {
            // it is supposed that very few entries are changed
            final Set<Integer> changedEntries = new HashSet<Integer> ();
            for ( final String calculationMethod : calculationMethods )
            {
                final Value[] lastValues = lastData.get ( calculationMethod );
                final Value[] values = data.get ( calculationMethod );
                for ( int i = 0; i < valueInformations.length; i++ )
                {
                    if ( !lastValueInformations[i].equals ( valueInformations[i] ) || !lastValues[i].equals ( values[i] ) )
                    {
                        changedEntries.add ( i );
                    }
                }
            }
            for ( final int changedIndex : changedEntries )
            {
                final Map<String, Value[]> subData = new HashMap<String, Value[]> ();
                for ( final String calculationMethod : calculationMethods )
                {
                    subData.put ( calculationMethod, new Value[] { data.get ( calculationMethod )[changedIndex] } );
                }
                listener.updateData ( changedIndex, subData, new ValueInformation[] { valueInformations[changedIndex] } );
            }
        }

        // update state to complete (call multiple times has no effect)
        setQueryState ( QueryState.COMPLETE );

        // remember processed data for next call of method
        lastParameters = parameters;
        lastValueInformations = valueInformations;
        lastData = data;
    }

    /**
     * This method marks the values that are affected by the specified time as changed
     * @param time time at which the affected values have to be marked as changed
     */
    private void markTimeAsDirty ( final long time )
    {
        synchronized ( service )
        {
            if ( !closed )
            {
                if ( parameters.getEndTimestamp ().getTimeInMillis () > time )
                {
                }
            }
        }
    }

    /**
     * @see org.openscada.hd.Query#changeParameters
     */
    public void changeParameters ( final QueryParameters parameters )
    {
        synchronized ( service )
        {
            this.parameters = parameters;
            setQueryState ( QueryState.LOADING );
        }
    }

    /**
     * @see org.openscada.hd.Query#close
     */
    public void close ()
    {
        synchronized ( service )
        {
            if ( !closed )
            {
                if ( queryTask != null )
                {
                    queryTask.shutdown ();
                    queryTask = null;
                }
                setQueryState ( QueryState.DISCONNECTED );
                if ( queryRegistered )
                {
                    service.removeQuery ( this );
                }
            }
            closed = true;
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public void cleanupRelicts () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getLongValues
     */
    public LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getMetaData
     */
    public StorageChannelMetaData getMetaData () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLong
     */
    public void updateLong ( final LongValue longValue ) throws Exception
    {
        if ( longValue != null )
        {
            markTimeAsDirty ( longValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLongs
     */
    public void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( longValues != null )
        {
            for ( final LongValue longValue : longValues )
            {
                updateLong ( longValue );
            }
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        if ( doubleValue != null )
        {
            markTimeAsDirty ( doubleValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( doubleValues != null )
        {
            for ( final DoubleValue doubleValue : doubleValues )
            {
                updateDouble ( doubleValue );
            }
        }
    }
}
