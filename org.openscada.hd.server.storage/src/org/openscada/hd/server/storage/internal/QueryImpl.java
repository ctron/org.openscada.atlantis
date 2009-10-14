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
import java.util.concurrent.FutureTask;

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
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.utils.ValueArrayNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the internal implementation of the HD query interface.
 * @see org.openscada.hd.Query
 * @author Ludwig Straub
 */
public class QueryImpl implements Query, ExtendedStorageChannel, Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    /** Time span between two consecutive calls of the future task. */
    private final static long DELAY_BETWEEN_TWO_QUERY_CALCULATIONS = 1000;

    /** Service that created the query object. */
    private final ShiService service;

    /** Listener that should receive the data. */
    private final QueryListener listener;

    /** Input parameters of the query. */
    private QueryParameters parameters;

    /** Flag indicating whether the result should be periodically updated or not. */
    private final boolean updateDataPeriodically;

    /** Flag indicating whether the query is closed or not. */
    private boolean closed;

    /** Flag indicating whether the query has to be executed again due to data changes. */
    private boolean updateRequired;

    /** Task that will calculate the result. */
    private FutureTask<Object> futureTask;

    /**
     * Constructor.
     * @param service service that created the query object
     * @param listener listener that should receive the data
     * @param parameters input parameters of the query
     * @param updateData flag indicating whether the result should be periodically updated or not
     */
    public QueryImpl ( final ShiService service, final QueryListener listener, final QueryParameters parameters, final boolean updateData )
    {
        this.service = service;
        this.listener = listener;
        this.parameters = parameters;
        this.updateDataPeriodically = updateData;
        this.closed = ( service == null ) || ( listener == null ) || ( parameters == null );
        if ( closed )
        {
            logger.error ( "not all data is available to execute query. no action will be performed" );
            if ( listener != null )
            {
                listener.updateState ( QueryState.DISCONNECTED );
            }
        }
        this.updateRequired = true;
        listener.updateState ( QueryState.LOADING );
        checkStartTask ();
    }

    /**
     * This method checks if the future task has to be started or not.
     * The task has to be started if it is not yet running and the query is not closed, an update is required due to either storage channel data change or query parameter changes.
     * If the task has to be started, it will be started by this method.
     */
    private void checkStartTask ()
    {
        if ( !closed && updateRequired && ( futureTask == null ) )
        {
            futureTask = new FutureTask<Object> ( this, null );
        }
    }

    /**
     * This method processes the query.
     */
    public void run ()
    {
        try
        {
            QueryParameters parameters = null;
            synchronized ( this )
            {
                parameters = this.parameters;
            }
            final long requestedEntries = parameters.getEntries ();
            if ( requestedEntries < 1 )
            {
                listener.updateState ( QueryState.COMPLETE );
            }
            else
            {
                // set the state to loading
                listener.updateState ( QueryState.LOADING );

                // retrieve data of level that is suitable for the requested time span
                final long startTime = parameters.getStartTimestamp ().getTimeInMillis ();
                final long endTime = parameters.getEndTimestamp ().getTimeInMillis ();
                final long requestedTimeSpan = endTime - startTime;
                final double requestedValueFrequency = (double)requestedTimeSpan / requestedEntries;
                final long optimalResultColumnCount = Math.max ( 1, requestedTimeSpan / requestedEntries );
                double currentTimeOffsetAsDouble = startTime;
                long currentTimeOffsetAsLong = startTime;
                long currentCompressionLevel = Long.MAX_VALUE;
                long currentResultColumnCount = Long.MIN_VALUE;
                do
                {
                    final Map<StorageChannelMetaData, BaseValue[]> availableChannels = service.getValues ( currentCompressionLevel, startTime, endTime );
                    final Map<String, Value[]> resultMap = new HashMap<String, Value[]> ();
                    final Set<String> calculationMethods = new HashSet<String> ();
                    if ( !availableChannels.isEmpty () )
                    {
                        boolean metaInformationCalculated = false;
                        ValueInformation[] resultValueInformationArray = null;
                        // get raw storage channel data from service
                        for ( Entry<StorageChannelMetaData, BaseValue[]> entry : availableChannels.entrySet () )
                        {
                            // get current compression level
                            final StorageChannelMetaData metaData = entry.getKey ();
                            CalculationLogicProvider calculationLogicProvider = Conversions.getCalculationLogicProvider ( metaData );
                            final BaseValue[] values = entry.getValue ();
                            currentCompressionLevel = Math.min ( currentCompressionLevel, metaData.getDetailLevelId () );
                            currentResultColumnCount = Math.max ( currentResultColumnCount, values.length );
                            final List<BaseValue> resultValues = new ArrayList<BaseValue> ();
                            while ( currentTimeOffsetAsLong < endTime )
                            {
                                currentTimeOffsetAsDouble += requestedValueFrequency;
                                final BaseValue[] filledValues = ValueArrayNormalizer.extractSubArray ( values, currentTimeOffsetAsLong, Math.max ( currentTimeOffsetAsLong + 1, (long)currentTimeOffsetAsDouble ), values instanceof LongValue[] ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                                final BaseValue[] normalizedValues = calculationLogicProvider.generateValues ( filledValues );
                                resultValues.addAll ( Arrays.asList ( normalizedValues ) );
                                currentTimeOffsetAsDouble += requestedValueFrequency;
                            }
                            Value[] resultValueArray = new Value[resultValues.size ()];
                            if ( !metaInformationCalculated )
                            {
                                resultValueInformationArray = new ValueInformation[resultValueArray.length];
                            }
                            if ( values instanceof LongValue[] )
                            {
                                LongValue[] longValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY );
                                for ( int i = 0; i < longValues.length; i++ )
                                {
                                    LongValue longValue = longValues[i];
                                    resultValueArray[i] = new Value ( longValue.getValue () );
                                    if ( !metaInformationCalculated )
                                    {
                                        Calendar cstartTime = Calendar.getInstance ();
                                        Calendar cendTime = Calendar.getInstance ();
                                        cstartTime.setTimeInMillis ( longValue.getTime () );
                                        cendTime.setTimeInMillis ( i == longValues.length - 1 ? endTime : longValues[i + 1].getTime () );
                                        resultValueInformationArray[i] = new ValueInformation ( cstartTime, cendTime, longValue.getQualityIndicator (), longValue.getBaseValueCount () );
                                    }
                                }
                            }
                            else
                            {
                                DoubleValue[] doubleValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                                for ( int i = 0; i < doubleValues.length; i++ )
                                {
                                    DoubleValue doubleValue = doubleValues[i];
                                    resultValueArray[i] = new Value ( doubleValue.getValue () );
                                    if ( !metaInformationCalculated )
                                    {
                                        Calendar cstartTime = Calendar.getInstance ();
                                        Calendar cendTime = Calendar.getInstance ();
                                        cstartTime.setTimeInMillis ( doubleValue.getTime () );
                                        cendTime.setTimeInMillis ( i == doubleValues.length - 1 ? endTime : doubleValues[i + 1].getTime () );
                                        resultValueInformationArray[i] = new ValueInformation ( cstartTime, cendTime, doubleValue.getQualityIndicator (), doubleValue.getBaseValueCount () );
                                    }
                                }
                            }
                            metaInformationCalculated = true;
                            String calculationMethod = CalculationMethod.convertCalculationMethodToShortString ( metaData.getCalculationMethod () );
                            calculationMethods.add ( calculationMethod );
                            resultMap.put ( calculationMethod, resultValueArray );
                        }

                        // send data to listener
                        synchronized ( this )
                        {
                            if ( closed )
                            {
                                return;
                            }
                            listener.updateParameters ( parameters, calculationMethods );
                            listener.updateState ( QueryState.COMPLETE );
                        }
                        currentCompressionLevel--;

                        // stop if a higher detail level won't result in great improvement of result
                        if ( currentResultColumnCount >= optimalResultColumnCount )
                        {
                            break;
                        }
                    }
                } while ( currentCompressionLevel >= 0 );
            }

            // wait for some time
            try
            {
                Thread.sleep ( DELAY_BETWEEN_TWO_QUERY_CALCULATIONS );
            }
            catch ( Exception e )
            {
                logger.debug ( "query thread was interrupted" );
            }

            // free related future task and restart query if new data is available
            synchronized ( this )
            {
                futureTask = null;
                checkStartTask ();
            }
        }
        catch ( Exception e )
        {
            logger.error ( "problem while processing query", e );
            synchronized ( this )
            {
                listener.updateState ( QueryState.DISCONNECTED );
                close ();
                futureTask = null;
            }
        }
    }

    /**
     * This method marks the values that are affected by the specified time as changed
     * @param time time at which the affected values have to be marked as changed
     */
    private void markTimeAsDirty ( final long time )
    {
        if ( updateDataPeriodically && !closed )
        {
            updateRequired = true;
        }
    }

    /**
     * @see org.openscada.hd.Query#changeParameters
     */
    public synchronized void changeParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;
        updateRequired = true;
        checkStartTask ();
    }

    /**
     * @see org.openscada.hd.Query#close
     */
    public synchronized void close ()
    {
        if ( !closed )
        {
            listener.updateState ( QueryState.DISCONNECTED );
            service.removeQuery ( this );
        }
        closed = true;
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        if ( longValue != null )
        {
            markTimeAsDirty ( longValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( longValues != null )
        {
            for ( LongValue longValue : longValues )
            {
                updateLong ( longValue );
            }
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        if ( doubleValue != null )
        {
            markTimeAsDirty ( doubleValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( doubleValues != null )
        {
            for ( DoubleValue doubleValue : doubleValues )
            {
                updateDouble ( doubleValue );
            }
        }
    }
}
