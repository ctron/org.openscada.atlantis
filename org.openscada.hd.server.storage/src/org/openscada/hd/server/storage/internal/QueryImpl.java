package org.openscada.hd.server.storage.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
import org.openscada.hsdb.concurrent.HsdbThreadFactory;
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
public class QueryImpl implements Query, ExtendedStorageChannel
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    /** Time span between two consecutive calls of the future task. */
    private final static long DELAY_BETWEEN_TWO_QUERY_CALCULATIONS = 1000;

    /** Task for processing the query. */
    private final static String QUERY_DATA_PROCESSOR_THREAD_ID = "QueryProcessor";

    /** Task for sending the query data. */
    private final static String QUERY_DATA_SENDER_THREAD_ID = "QueryDataSender";

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

    /** Maximum available compression level. */
    private final long maximumCompressionLevel;

    /** Flag indicating whether data was changed or not. */
    private boolean initialLoadPerformed;

    /** Flag indicating whether data was changed or not. */
    private boolean dataChanged;

    /** Set of indices of start times that have to be calculated again. */
    private final Set<Integer> startTimeIndicesToUpdate;

    /** This attribute is set by the method calculateValues and read by the method sendCalculatedValues. It contains the value information objects that were created during the calculation process. */
    private ValueInformation[] calculatedResultValueInformations;

    /** This attribute is set by the method calculateValues and read by the method sendCalculatedValues. It contains the value objects that were created during the calculation process. */
    private Map<String, Value[]> calculatedResultMap;

    /** Latest value that was processed or re-processed. */
    private long latestDirtyTime;

    /** Executor that will be used to send data. */
    private ExecutorService sendingTask;

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
        this.calculationMethods = new HashSet<CalculationMethod> ( calculationMethods );
        maximumCompressionLevel = service.getMaximumCompressionLevel ();
        startTimeIndicesToUpdate = new HashSet<Integer> ();
        initialLoadPerformed = false;
        this.closed = ( service == null ) || ( listener == null );
        if ( closed )
        {
            logger.error ( "not all data is available to execute query via 'new query'. no action will be performed" );
            setQueryState ( QueryState.DISCONNECTED );
            queryRegistered = false;
            dataChanged = false;
        }
        else
        {
            sendingTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( QUERY_DATA_SENDER_THREAD_ID ) );
            setQueryState ( QueryState.LOADING );
            latestDirtyTime = Long.MAX_VALUE;
            dataChanged = true;
            final Runnable runnable = new Runnable () {
                public void run ()
                {
                    processQuery ();
                }
            };
            queryTask = Executors.newSingleThreadScheduledExecutor ( HsdbThreadFactory.createFactory ( QUERY_DATA_PROCESSOR_THREAD_ID ) );
            if ( updateData )
            {
                service.addQuery ( this );
                queryRegistered = true;
                queryTask.scheduleWithFixedDelay ( runnable, 0, DELAY_BETWEEN_TWO_QUERY_CALCULATIONS, TimeUnit.MILLISECONDS );
            }
            else
            {
                queryRegistered = false;
                queryTask.schedule ( runnable, 0, TimeUnit.MILLISECONDS );
            }
        }
    }

    /**
     * This method updates the query state if the new state differs from the current state.
     * @param queryState new query state
     */
    private void setQueryState ( final QueryState queryState )
    {
        if ( ( listener != null ) && ( currentQueryState == null ) || ( currentQueryState != queryState ) )
        {
            currentQueryState = queryState;
            listener.updateState ( queryState );
        }
    }

    /**
     * This method removes elements from the end of the second array until there is no more time overlapping between the first and the second array.
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
            final LongValue[] secondArray1 = (LongValue[])secondArray;
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
     * This method loads data from the service and calculates the output.
     * @param startTime start time for calculation
     * @param endTime end time for calculation
     * @param resultSize size of entries per calculation method during the requested time span
     * @throws Exception in case of problems when retrieving data via the service
     */
    private void calculateValues ( final long startTime, final long endTime, final int resultSize ) throws Exception
    {
        Map<StorageChannelMetaData, BaseValue[]> mergeMap = null;
        synchronized ( service )
        {
            // load raw data that has to be normalized later
            latestDirtyTime = System.currentTimeMillis ();
            long currentCompressionLevel = 0;
            long oldestValueTime = Long.MAX_VALUE;
            final BaseValue latestValue = service.getLatestValue ();
            final long latestValidTime = latestValue == null ? latestDirtyTime : latestValue.getTime ();
            while ( ( oldestValueTime > startTime ) && ( currentCompressionLevel <= maximumCompressionLevel ) )
            {
                if ( currentCompressionLevel == 0 )
                {
                    mergeMap = service.getValues ( currentCompressionLevel, startTime, endTime );
                    for ( final Entry<StorageChannelMetaData, BaseValue[]> mergeEntry : mergeMap.entrySet () )
                    {
                        final DataType dataType = mergeEntry.getKey ().getDataType ();
                        final BaseValue[] mergeValues = mergeEntry.getValue ();
                        final long maxTime = mergeValues.length > 0 ? Math.max ( latestValidTime + 1, latestDirtyTime ) : latestDirtyTime;
                        latestDirtyTime = Math.min ( maxTime, latestDirtyTime );
                        if ( dataType == DataType.LONG_VALUE )
                        {
                            final LongValue longValue = new LongValue ( maxTime, 0, 0, 0 );
                            mergeEntry.setValue ( joinValueArrays ( new LongValue[] { longValue }, mergeValues ) );
                        }
                        else
                        {
                            final DoubleValue doubleValue = new DoubleValue ( maxTime, 0, 0, 0 );
                            mergeEntry.setValue ( joinValueArrays ( new DoubleValue[] { doubleValue }, mergeValues ) );
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
                                mergeEntry.setValue ( joinValueArrays ( mergeValues, removeTimeOverlay ( mergeValues, subEntry.getValue () ) ) );
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
            dataChanged = false;
            initialLoadPerformed = true;
        }

        // since all data is collected now, the normalizing can be performed
        final MutableValueInformation[] resultValueInformations = new MutableValueInformation[resultSize];
        for ( int i = 0; i < resultValueInformations.length; i++ )
        {
            resultValueInformations[i] = new MutableValueInformation ( null, null, 1.0, Long.MAX_VALUE );
        }
        final Map<String, Value[]> resultMap = new HashMap<String, Value[]> ();
        for ( final CalculationMethod calculationMethod : calculationMethods )
        {
            resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), new Value[0] );
        }
        final long requestedTimeSpan = endTime - startTime;
        final double requestedValueFrequency = (double)requestedTimeSpan / resultSize;

        // get raw storage channel data from service
        for ( final Entry<StorageChannelMetaData, BaseValue[]> entry : mergeMap.entrySet () )
        {
            // get current compression level
            final StorageChannelMetaData metaData = entry.getKey ();
            final CalculationLogicProvider calculationLogicProvider = Conversions.getCalculationLogicProvider ( metaData );
            final BaseValue[] values = entry.getValue ();
            int startIndex = 0;
            final List<BaseValue> resultValues = new ArrayList<BaseValue> ();
            final DataType inputDataType = calculationLogicProvider.getInputType ();
            final DataType outputDataType = calculationLogicProvider.getOutputType ();
            for ( int i = 0; i < resultSize; i++ )
            {
                final double currentTimeOffsetAsDouble = startTime + i * requestedValueFrequency;
                final long currentTimeOffsetAsLong = Math.round ( currentTimeOffsetAsDouble );
                final long localEndTime = Math.round ( currentTimeOffsetAsDouble + requestedValueFrequency );
                BaseValue[] filledValues = null;
                if ( values.length == 0 )
                {
                    if ( inputDataType == DataType.LONG_VALUE )
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
                    filledValues = ValueArrayNormalizer.extractSubArray ( values, currentTimeOffsetAsLong, localEndTime <= currentTimeOffsetAsLong ? currentTimeOffsetAsLong + 1 : localEndTime, startIndex, inputDataType == DataType.LONG_VALUE ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                    // maximum 2 entries are completely virtual due to the algorithm
                    // it is possible that one value will be processed with a time span before the interval start time
                    // therefore the index can be increased by length-3 to optimize performance of this method
                    if ( filledValues.length > 3 )
                    {
                        startIndex += filledValues.length - 3;
                    }
                    if ( filledValues == null || filledValues.length == 0 )
                    {
                        filledValues = ValueArrayNormalizer.extractSubArray ( values, currentTimeOffsetAsLong, localEndTime, startIndex, inputDataType == DataType.LONG_VALUE ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                    }
                }
                final BaseValue normalizedValue = calculationLogicProvider.generateValues ( filledValues );
                if ( normalizedValue != null )
                {
                    resultValues.add ( normalizedValue );
                }
                else
                {
                    logger.error ( "DF" );
                }
            }
            final Value[] resultValueArray = new Value[resultSize];
            if ( outputDataType == DataType.LONG_VALUE )
            {
                final LongValue[] longValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY );
                for ( int i = 0; i < resultValueArray.length; i++ )
                {
                    final LongValue longValue = longValues[i];
                    resultValueArray[i] = new Value ( longValue.getValue () );
                    final Calendar cstartTime = Calendar.getInstance ();
                    final Calendar cendTime = Calendar.getInstance ();
                    cstartTime.setTimeInMillis ( longValue.getTime () );
                    cendTime.setTimeInMillis ( i == longValues.length - 1 ? endTime : longValues[i + 1].getTime () );
                    final MutableValueInformation valueInformation = resultValueInformations[i];
                    valueInformation.setStartTimestamp ( cstartTime );
                    valueInformation.setEndTimestamp ( cendTime );
                    valueInformation.setQuality ( Math.min ( longValue.getQualityIndicator (), valueInformation.getQuality () ) );
                    valueInformation.setSourceValues ( Math.min ( longValue.getBaseValueCount (), valueInformation.getSourceValues () ) );
                }
            }
            else
            {
                final DoubleValue[] doubleValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                if ( resultValueArray.length != doubleValues.length )
                {
                    logger.error ( "result values are nto equal in length to generated values!" );
                }
                for ( int i = 0; i < resultValueArray.length; i++ )
                {
                    final DoubleValue doubleValue = doubleValues[i];
                    resultValueArray[i] = new Value ( doubleValue.getValue () );
                    final Calendar cstartTime = Calendar.getInstance ();
                    final Calendar cendTime = Calendar.getInstance ();
                    cstartTime.setTimeInMillis ( doubleValue.getTime () );
                    cendTime.setTimeInMillis ( i == doubleValues.length - 1 ? endTime : doubleValues[i + 1].getTime () );
                    final MutableValueInformation valueInformation = resultValueInformations[i];
                    valueInformation.setStartTimestamp ( cstartTime );
                    valueInformation.setEndTimestamp ( cendTime );
                    valueInformation.setQuality ( Math.min ( doubleValue.getQualityIndicator (), valueInformation.getQuality () ) );
                    valueInformation.setSourceValues ( Math.min ( doubleValue.getBaseValueCount (), valueInformation.getSourceValues () ) );
                }
            }
            resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( metaData.getCalculationMethod () ), resultValueArray );
        }
        this.calculatedResultValueInformations = new ValueInformation[resultValueInformations.length];
        for ( int i = 0; i < resultValueInformations.length; i++ )
        {
            final MutableValueInformation valueInformation = resultValueInformations[i];
            this.calculatedResultValueInformations[i] = new ValueInformation ( valueInformation.getStartTimestamp (), valueInformation.getEndTimestamp (), valueInformation.getQuality (), valueInformation.getSourceValues () );
        }
        this.calculatedResultMap = resultMap;
    }

    /**
     * This method sends the calculated data to the listener.
     * @param parameters parameters that were used to generate the result
     * @param startIndex start index of data that has to be transferred
     * @param updateParameters flag indicating whether the method updateParameters of the listener should be triggered
     */
    public void sendCalculatedValues ( final QueryParameters parameters, final int startIndex, final boolean updateParameters )
    {
        // send data to listener
        final Map<String, Value[]> calculatedResultMap = this.calculatedResultMap;
        final ValueInformation[] calculatedResultValueInformations = this.calculatedResultValueInformations;
        synchronized ( service )
        {
            // stop immediately if query has been closed in the meantime
            if ( closed )
            {
                return;
            }

            // do not send any data if input parameters have changed
            if ( !parameters.equals ( this.parameters ) )
            {
                return;
            }

            // send data
            sendingTask.submit ( new Runnable () {
                public void run ()
                {
                    // prepare sending generated data
                    if ( updateParameters )
                    {
                        listener.updateParameters ( parameters, calculatedResultMap.keySet () );
                    }

                    // send generated data
                    listener.updateData ( startIndex, calculatedResultMap, calculatedResultValueInformations );

                    // update state to complete (call multiple times has no effect)
                    if ( initialLoadPerformed )
                    {
                        setQueryState ( QueryState.COMPLETE );
                    }
                }
            } );
        }
    }

    /**
     * This method processes the query.
     */
    private synchronized void processQuery ()
    {
        try
        {
            // prepare all data that is required for calculation
            QueryParameters parameters = null;
            boolean initialLoadPerformed;
            Set<Integer> startTimeIndicesToUpdate = null;
            synchronized ( service )
            {
                if ( !dataChanged )
                {
                    return;
                }
                initialLoadPerformed = this.initialLoadPerformed;
                parameters = this.parameters;
                if ( initialLoadPerformed )
                {
                    startTimeIndicesToUpdate = new HashSet<Integer> ( this.startTimeIndicesToUpdate );
                }
                if ( !this.startTimeIndicesToUpdate.isEmpty () )
                {
                    this.startTimeIndicesToUpdate.clear ();
                }
                final Calendar start = parameters.getStartTimestamp ();
                final Calendar end = parameters.getEndTimestamp ();
                if ( ( parameters == null ) || ( start == null ) || ( end == null ) || ( parameters.getEntries () < 1 ) )
                {
                    this.initialLoadPerformed = true;
                    this.dataChanged = false;
                    final Set<String> calculationMethodsAsString = new HashSet<String> ();
                    for ( final CalculationMethod calculationMethod : calculationMethods )
                    {
                        calculationMethodsAsString.add ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ) );
                    }
                    listener.updateParameters ( parameters, calculationMethodsAsString );
                    setQueryState ( QueryState.COMPLETE );
                    return;
                }
            }
            if ( !initialLoadPerformed )
            {
                // calculate all values
                calculateValues ( parameters.getStartTimestamp ().getTimeInMillis (), parameters.getEndTimestamp ().getTimeInMillis () + 1, parameters.getEntries () );
                sendCalculatedValues ( parameters, 0, true );
            }
            else
            {
                // calculate selected values
                if ( startTimeIndicesToUpdate.isEmpty () )
                {
                    return;
                }
                final long startTime = parameters.getStartTimestamp ().getTimeInMillis ();
                final long endTime = parameters.getEndTimestamp ().getTimeInMillis ();
                final long requestedTimeSpan = endTime - startTime;
                final double requestedValueFrequency = (double)requestedTimeSpan / parameters.getEntries ();
                final Integer[] startTimeIndicesToUpdateArray = startTimeIndicesToUpdate.toArray ( new Integer[0] );
                Arrays.sort ( startTimeIndicesToUpdateArray );
                int index = 0;
                while ( index < startTimeIndicesToUpdateArray.length )
                {
                    final int startIndex = startTimeIndicesToUpdateArray[index];
                    int endIndex = startIndex;
                    while ( ( index + 1 < startTimeIndicesToUpdateArray.length ) && ( startTimeIndicesToUpdateArray[index + 1] == endIndex + 1 ) )
                    {
                        endIndex++;
                        index++;
                    }
                    final long resultSize = endIndex - startIndex + 1;
                    final double currentStartTimeAsDouble = startTime + startIndex * requestedValueFrequency;
                    final double currentEndTimeAsDouble = currentStartTimeAsDouble + requestedValueFrequency * ( endIndex - startIndex + 1 );
                    final long currentStartTimeAsLong = Math.round ( currentStartTimeAsDouble );
                    final long currentEndTimeAsLong = Math.round ( currentEndTimeAsDouble );
                    calculateValues ( currentStartTimeAsLong, currentEndTimeAsLong + 1, (int)resultSize );
                    sendCalculatedValues ( parameters, startIndex, false );
                    index++;
                }
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
     * This method marks the values that are affected by the specified time as changed
     * @param time time at which the affected values have to be marked as changed
     */
    private void markTimeAsDirty ( final long time )
    {
        synchronized ( service )
        {
            if ( !closed && initialLoadPerformed )
            {
                final long endTime = parameters.getEndTimestamp ().getTimeInMillis ();
                if ( time >= endTime )
                {
                    return;
                }
                dataChanged = true;
                final long startTime = parameters.getStartTimestamp ().getTimeInMillis ();
                if ( time <= startTime )
                {
                    startTimeIndicesToUpdate.add ( 0 );
                    return;
                }
                final long resultSize = parameters.getEntries ();
                final long requestedTimeSpan = endTime - startTime;
                final double requestedValueFrequency = (double)requestedTimeSpan / resultSize;
                for ( int i = 0; i < resultSize; i++ )
                {
                    final double currentStartTimeAsDouble = startTime + i * requestedValueFrequency;
                    final double currentEndTimeAsDouble = currentStartTimeAsDouble + requestedValueFrequency;
                    final long currentStartTimeAsLong = Math.round ( currentStartTimeAsDouble );
                    final long currentEndTimeAsLong = Math.round ( currentEndTimeAsDouble );
                    if ( ( latestDirtyTime <= currentEndTimeAsLong ) || ( ( currentStartTimeAsLong <= time ) && ( time <= currentEndTimeAsLong ) ) )
                    {
                        startTimeIndicesToUpdate.add ( i );
                    }
                    else if ( time < currentStartTimeAsLong )
                    {
                        break;
                    }
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
            this.dataChanged = true;
            this.initialLoadPerformed = false;
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
                if ( sendingTask != null )
                {
                    sendingTask.shutdown ();
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
