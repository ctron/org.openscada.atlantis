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
import org.openscada.hd.server.storage.StorageHistoricalItemService;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.calculation.CalculationLogicProviderFactoryImpl;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.concurrent.HsdbThreadFactory;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.utils.HsdbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the internal implementation of the HD query interface.
 * The class works in direct combination with the class StorageHistoricalItemService.
 * Therefore synchronization is done via the related service object.
 * @see org.openscada.hd.Query
 * @see org.openscada.hd.server.storage.StorageHistoricalItemService
 * @author Ludwig Straub
 */
public class QueryImpl implements Query, ExtendedStorageChannel
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    /** Time span between two consecutive calls of the future task. */
    private final static long DELAY_BETWEEN_TWO_QUERY_CALCULATIONS = 1000;

    /** Task for processing the query. */
    private final static String QUERY_DATA_PROCESSOR_THREAD_ID = "hd.QueryProcessor";

    /** Task for receiving the query parameter changes. */
    private final static String QUERY_DATA_RECEIVER_THREAD_ID = "hd.QueryParameterChangeReceiver";

    /** Task for sending the query data. */
    private final static String QUERY_DATA_SENDER_THREAD_ID = "hd.QueryDataSender";

    /** Task for closing the query. */
    private final static String QUERY_CLOSER_THREAD_ID = "hd.QueryCloser";

    /** Service that created the query object. */
    private final StorageHistoricalItemService service;

    /** Map containing all available storage channels mapped by detail level id and calculation method including the calculation logic provider objects. */
    private Map<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> storageChannels;

    /** Listener that should receive the data. */
    private final QueryListener listener;

    /** Set of available calculation methods. */
    private final CalculationMethod[] calculationMethods;

    /** Input parameters of the query. */
    private QueryParameters parameters;

    /** Currently set query state. */
    private volatile QueryState currentQueryState;

    /** Flag indicating whether the query is closed or not. */
    private volatile boolean closed;

    /** Task that will calculate the result. */
    private ScheduledExecutorService queryTask;

    /** Task that will close the query. */
    private ExecutorService closerTask;

    /** Flag indicating whether data was changed or not. */
    private boolean initialLoadPerformed;

    /** Flag indicating whether data was changed or not. */
    private boolean dataChanged;

    /** Set of indices of start times that have to be calculated again. */
    private final Set<Integer> startTimeIndicesToUpdate;

    /** Latest value that was processed or re-processed. */
    private long latestDirtyTime;

    /** Executor that will be used to send data. */
    private ExecutorService sendingTask;

    /** Executor that will be used when changing parameters. */
    private ExecutorService receivingTask;

    /** Parameters for which data was sent last time. */
    private QueryParameters lastParameters;

    /** Data that was sent last time. */
    private Map<String, Value[]> lastData;

    /** Value information that was sent together with data last time. */
    private ValueInformation[] lastValueInformations;

    /** Factory that will be used when creating new calculation logic provider objects. */
    private final CalculationLogicProviderFactoryImpl calculationLogicProviderFactory;

    /** Maximum compression level. */
    private long maximumCompressionLevel;

    private volatile boolean updateData = false;

    /**
     * Constructor.
     * @param service service that created the query object
     * @param listener listener that should receive the data
     * @param parameters input parameters of the query
     * @param calculationMethods set of calculation methods that will be available via the service
     */
    public QueryImpl ( final StorageHistoricalItemService service, final QueryListener listener, final QueryParameters parameters, final CalculationMethod[] calculationMethods )
    {
        this.service = service;
        this.listener = listener;
        this.parameters = parameters;
        this.calculationMethods = calculationMethods.clone ();
        startTimeIndicesToUpdate = new HashSet<Integer> ();
        initialLoadPerformed = false;
        maximumCompressionLevel = 0;
        calculationLogicProviderFactory = new CalculationLogicProviderFactoryImpl ();
        closed = ( service == null ) || ( listener == null ) || ( parameters == null ) || ( parameters.getStartTimestamp () == null ) || ( parameters.getEndTimestamp () == null );
        if ( closed )
        {
            logger.error ( "not all data is available to execute query via 'new query'. no action will be performed" );
            setQueryState ( QueryState.DISCONNECTED );
            dataChanged = false;
        }
        else
        {
            setQueryState ( QueryState.LOADING );
        }
    }

    /**
     * This method starts the query execution.
     * @param storageChannels map containing all available storage channels mapped by detail level id and calculation method including the calculation logic provider objects
     * @param updateData flag indicating whether the result should be periodically updated or not
     */
    public void run ( final Map<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> storageChannels, final boolean updateData )
    {
        this.updateData = updateData;
        if ( closed )
        {
            return;
        }
        this.storageChannels = storageChannels;
        for ( final Long compressionLevel : storageChannels.keySet () )
        {
            maximumCompressionLevel = Math.max ( maximumCompressionLevel, compressionLevel );
        }
        receivingTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( QUERY_DATA_RECEIVER_THREAD_ID ) );
        sendingTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( QUERY_DATA_SENDER_THREAD_ID ) );
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
            queryTask.scheduleWithFixedDelay ( runnable, 0, DELAY_BETWEEN_TWO_QUERY_CALCULATIONS, TimeUnit.MILLISECONDS );
        }
        else
        {
            queryTask.schedule ( runnable, 0, TimeUnit.MILLISECONDS );
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
     * This method loads data from the service and calculates the output.
     * @param absoluteStartTime start time of global calculation
     * @param absoluteEndTime end time of global calculation
     * @param absoluteResultSize size of entries per global calculation
     * @param firstIndex index of first time slice of the global time span that has to be calculated
     * @param lastIndex index of last time slice of the global time span that has to be calculated
     * @throws Exception in case of problems when retrieving data via the service
     */
    private CalculatedData calculateValues ( final long absoluteStartTime, final long absoluteEndTime, final int absoluteResultSize, final int firstIndex, final int lastIndex ) throws Exception
    {
        // calculate global values
        final long requestedTimeSpan = absoluteEndTime - absoluteStartTime;
        final double requestedValueFrequency = (double)requestedTimeSpan / absoluteResultSize;
        final int resultSize = lastIndex - firstIndex + 1;
        final double startTimeAsDouble = absoluteStartTime + firstIndex * requestedValueFrequency;
        final double endTimeAsDouble = absoluteStartTime + ( lastIndex + 1 ) * requestedValueFrequency;
        final long startTime = Math.round ( startTimeAsDouble );
        final long endTime = Math.min ( absoluteEndTime, ( (long)endTimeAsDouble ) + 1 );

        // perform calculation
        Map<StorageChannelMetaData, BaseValue[]> mergeMap = null;

        // load raw data that has to be normalized later
        latestDirtyTime = System.currentTimeMillis ();
        long currentCompressionLevel = getRecommendedCompressionLevel ( ( parameters.getEndTimestamp ().getTimeInMillis () - parameters.getStartTimestamp ().getTimeInMillis () ) / parameters.getEntries () );
        long oldestValueTime = Long.MAX_VALUE;
        final BaseValue latestValue = getLatestValue ();
        final long latestValidTime = latestValue == null ? latestDirtyTime : latestValue.getTime ();
        boolean firstIteration = true;
        while ( ( oldestValueTime > startTime ) && ( currentCompressionLevel <= maximumCompressionLevel ) )
        {
            if ( firstIteration )
            {
                firstIteration = false;
                mergeMap = getValues ( currentCompressionLevel, startTime, endTime );
                for ( final Entry<StorageChannelMetaData, BaseValue[]> mergeEntry : mergeMap.entrySet () )
                {
                    final BaseValue[] mergeValues = mergeEntry.getValue ();
                    final long maxTime = mergeValues.length > 0 ? Math.max ( latestValidTime + 1, latestDirtyTime ) : latestDirtyTime;
                    if ( ( maxTime == latestDirtyTime ) && ( maxTime < absoluteEndTime ) )
                    {
                        markTimeAsDirty ( maxTime, false );
                    }
                    latestDirtyTime = Math.min ( maxTime, latestDirtyTime );
                    if ( mergeValues instanceof LongValue[] )
                    {
                        final LongValue longValue = new LongValue ( maxTime, 0, 0, 0, 0 );
                        mergeEntry.setValue ( QueryHelper.joinValueArrays ( new LongValue[] { longValue }, mergeValues ) );
                    }
                    else
                    {
                        final DoubleValue doubleValue = new DoubleValue ( maxTime, 0, 0, 0, 0 );
                        mergeEntry.setValue ( QueryHelper.joinValueArrays ( new DoubleValue[] { doubleValue }, mergeValues ) );
                    }
                }
            }
            else
            {
                final Map<StorageChannelMetaData, BaseValue[]> subMap = getValues ( currentCompressionLevel, startTime, Math.min ( oldestValueTime, endTime ) );
                for ( final Entry<StorageChannelMetaData, BaseValue[]> subEntry : subMap.entrySet () )
                {
                    for ( final Entry<StorageChannelMetaData, BaseValue[]> mergeEntry : mergeMap.entrySet () )
                    {
                        if ( mergeEntry.getKey ().getCalculationMethod () == subEntry.getKey ().getCalculationMethod () )
                        {
                            final BaseValue[] mergeValues = mergeEntry.getValue ();
                            mergeEntry.setValue ( QueryHelper.joinValueArrays ( mergeValues, QueryHelper.removeTimeOverlay ( mergeValues, subEntry.getValue () ) ) );
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

        // since all data is collected now, the normalizing can be performed
        final MutableValueInformation[] resultValueInformations = new MutableValueInformation[resultSize];
        for ( int i = 0; i < resultValueInformations.length; i++ )
        {
            resultValueInformations[i] = new MutableValueInformation ( null, null, 1.0, 0.0, Long.MAX_VALUE );
        }
        final Map<String, Value[]> resultMap = new HashMap<String, Value[]> ();
        for ( final CalculationMethod calculationMethod : calculationMethods )
        {
            resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), new Value[0] );
        }

        // get raw storage channel data from service
        for ( final Entry<StorageChannelMetaData, BaseValue[]> entry : mergeMap.entrySet () )
        {
            // get current compression level
            final StorageChannelMetaData metaData = entry.getKey ();
            final CalculationLogicProvider calculationLogicProvider = calculationLogicProviderFactory.getCalculationLogicProvider ( metaData );
            final BaseValue[] values = entry.getValue ();
            int startIndex = 0;
            final List<BaseValue> resultValues = new ArrayList<BaseValue> ();
            final DataType outputDataType = calculationLogicProvider.getOutputType ();
            for ( int i = 0; i < resultSize; i++ )
            {
                final double currentTimeOffsetAsDouble = startTimeAsDouble + i * requestedValueFrequency;
                final long currentTimeOffsetAsLong = Math.round ( currentTimeOffsetAsDouble );
                final long localEndTime = Math.round ( currentTimeOffsetAsDouble + requestedValueFrequency );
                final BaseValue[] filledValues = HsdbHelper.extractSubArray ( values, currentTimeOffsetAsLong, localEndTime <= currentTimeOffsetAsLong ? currentTimeOffsetAsLong + 1 : localEndTime, startIndex, values instanceof LongValue[] ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
                // maximum 2 entries are completely virtual due to the algorithm
                // it is possible that one value will be processed with a time span before the interval start time
                // therefore the index can be increased by length-3 to optimize performance of this method
                if ( filledValues.length > 3 )
                {
                    startIndex += filledValues.length - 3;
                }
                final long lastFilledValueTime = filledValues[filledValues.length - 1].getTime ();
                final long size = values.length;
                while ( ( startIndex + 1 < size ) && ( values[startIndex + 1].getTime () < lastFilledValueTime ) )
                {
                    startIndex++;
                }
                resultValues.add ( calculationLogicProvider.generateValue ( filledValues ) );
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
                    valueInformation.setManual ( Math.max ( longValue.getManualIndicator (), valueInformation.getManual () ) );
                    valueInformation.setSourceValues ( Math.min ( longValue.getBaseValueCount (), valueInformation.getSourceValues () ) );
                }
            }
            else
            {
                final DoubleValue[] doubleValues = resultValues.toArray ( ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY );
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
                    valueInformation.setManual ( Math.max ( doubleValue.getManualIndicator (), valueInformation.getManual () ) );
                    valueInformation.setSourceValues ( Math.min ( doubleValue.getBaseValueCount (), valueInformation.getSourceValues () ) );
                }
            }
            resultMap.put ( CalculationMethod.convertCalculationMethodToShortString ( metaData.getCalculationMethod () ), resultValueArray );
        }
        final CalculatedData calculatedData = new CalculatedData ( new ValueInformation[resultValueInformations.length], resultMap );
        final ValueInformation[] calculatedValueInformations = calculatedData.getValueInformations ();
        for ( int i = 0; i < resultValueInformations.length; i++ )
        {
            final MutableValueInformation valueInformation = resultValueInformations[i];
            calculatedValueInformations[i] = new ValueInformation ( valueInformation.getStartTimestamp (), valueInformation.getEndTimestamp (), valueInformation.getQuality (), valueInformation.getManual (), valueInformation.getSourceValues () );
        }
        return calculatedData;
    }

    /**
     * This method sends the calculated data to the listener.
     * @param parameters parameters that were used to generate the result
     * @param startIndex start index of data that has to be transferred
     * @param calculatedData data that has been calculated
     */
    public void sendCalculatedValues ( final QueryParameters parameters, final int startIndex, final CalculatedData calculatedData )
    {
        // send data to listener
        final ValueInformation[] calculatedResultValueInformations = calculatedData.getValueInformations ();
        final Map<String, Value[]> calculatedResultMap = calculatedData.getData ();

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
        sendDataDiff ( parameters, calculatedResultMap, calculatedResultValueInformations, startIndex );
    }

    /**
     * This method sends the passed data to the listener. If data was sent before, only the data difference is sent.
     * @param parameters parameters that have been used to generate the data
     * @param calculationMethods calculation methods for which data is available
     * @param data data mapped by calculation methods
     * @param valueInformations state information of generated data
     * @param startIndex first index to start comparing the data. all data before this index is supposed to be unchanged
     */
    private void sendDataDiff ( final QueryParameters parameters, final Map<String, Value[]> data, final ValueInformation[] valueInformations, final int startIndex )
    {
        // do not send any data if input parameters have changed
        if ( !parameters.equals ( this.parameters ) )
        {
            lastParameters = null;
            lastValueInformations = null;
            lastData = null;
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
            sendingTask.submit ( new Runnable () {
                public void run ()
                {
                    listener.updateParameters ( parameters, calculationMethods );
                }
            } );
        }

        // send generated data
        if ( sameParameters && sameCalculationMethods && ( lastData != null ) && ( lastValueInformations != null ) )
        {
            try
            {
                // collect all indices of which the data has changed
                final Set<Integer> changedEntriesSet = new HashSet<Integer> ();
                for ( final String calculationMethod : calculationMethods )
                {
                    final Value[] lastValues = lastData.get ( calculationMethod );
                    final Value[] values = data.get ( calculationMethod );
                    for ( int i = 0; i < valueInformations.length; i++ )
                    {
                        final int j = i + startIndex;
                        if ( !lastValueInformations[j].equals ( valueInformations[i] ) || !lastValues[j].equals ( values[i] ) )
                        {
                            changedEntriesSet.add ( j );
                            lastValueInformations[j] = valueInformations[i];
                            lastValues[j] = values[i];
                        }
                    }
                }

                // send changed value blocks as sub bulks
                final Integer[] changedEntries = changedEntriesSet.toArray ( new Integer[0] );
                Arrays.sort ( changedEntries );
                int index = 0;
                final int size = changedEntries.length;
                while ( index < size )
                {
                    final int startBlockIndex = changedEntries[index++];
                    int endBlockIndex = startBlockIndex;
                    int nextBlockIndex = index < size ? changedEntries[index] : endBlockIndex;
                    while ( nextBlockIndex == endBlockIndex + 1 )
                    {
                        index++;
                        endBlockIndex = nextBlockIndex;
                        nextBlockIndex = index < size ? changedEntries[index] : endBlockIndex;
                    }
                    final int blockSize = endBlockIndex - startBlockIndex + 1;
                    final Map<String, Value[]> subData = new HashMap<String, Value[]> ();
                    final ValueInformation[] valueInformationBlock = new ValueInformation[blockSize];
                    for ( final String calculationMethod : calculationMethods )
                    {
                        final Value[] valueBlock = new Value[blockSize];
                        for ( int i = startBlockIndex; i <= endBlockIndex; i++ )
                        {
                            final int absoluteIndex = i - startIndex;
                            final int absoluteBlockIndex = i - startBlockIndex;
                            valueInformationBlock[absoluteBlockIndex] = valueInformations[absoluteIndex];
                            valueBlock[absoluteBlockIndex] = data.get ( calculationMethod )[absoluteIndex];
                        }
                        subData.put ( calculationMethod, valueBlock );
                    }
                    sendingTask.submit ( new Runnable () {
                        public void run ()
                        {
                            listener.updateData ( startBlockIndex, subData, valueInformationBlock );
                            setQueryState ( QueryState.COMPLETE );
                        }
                    } );
                }

                // remember processed data for next call of method
                lastParameters = parameters;
                return;
            }
            catch ( final Exception e )
            {
                // optimistic data locking failed, but no real problem
                logger.info ( "base data changed since last calculation of data diff. sending complete set of data..." );
            }
        }

        // send complete data
        sendingTask.submit ( new Runnable () {
            public void run ()
            {
                listener.updateData ( startIndex, data, valueInformations );
                setQueryState ( QueryState.COMPLETE );
            }
        } );
        lastParameters = parameters;
        lastValueInformations = valueInformations;
        lastData = data;
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
            final boolean initialLoadPerformed;
            Set<Integer> startTimeIndicesToUpdate = null;
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
                dataChanged = false;
                final Set<String> calculationMethodsAsString = new HashSet<String> ();
                for ( final CalculationMethod calculationMethod : calculationMethods )
                {
                    calculationMethodsAsString.add ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ) );
                }
                listener.updateParameters ( parameters, calculationMethodsAsString );
                setQueryState ( QueryState.COMPLETE );
                return;
            }
            if ( !initialLoadPerformed )
            {
                // calculate all values
                final CalculatedData calculatedData = calculateValues ( parameters.getStartTimestamp ().getTimeInMillis (), parameters.getEndTimestamp ().getTimeInMillis () + 1, parameters.getEntries (), 0, parameters.getEntries () - 1 );
                sendCalculatedValues ( parameters, 0, calculatedData );
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
                    final CalculatedData calculatedData = calculateValues ( startTime, endTime + 1, parameters.getEntries (), startIndex, endIndex );
                    sendCalculatedValues ( parameters, startIndex, calculatedData );
                    index++;
                }
            }
        }
        catch ( final Exception e )
        {
            logger.error ( "problem while processing query", e );
            setQueryState ( QueryState.DISCONNECTED );
            close ();
        }
    }

    /**
     * This method marks the values that are affected by the specified time as changed
     * @param time time at which the affected values have to be marked as changed
     * @param invalidateAllData flag indicating whether all values should also be calculated again or not
     */
    private void markTimeAsDirty ( final long time, final boolean invalidateAllData )
    {
        if ( closed )
        {
            return;
        }
        final long endTime = parameters.getEndTimestamp ().getTimeInMillis ();
        final long startTime = parameters.getStartTimestamp ().getTimeInMillis ();
        dataChanged = true;
        if ( !invalidateAllData && ( time <= startTime ) )
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
            if ( invalidateAllData || ( latestDirtyTime <= currentEndTimeAsLong ) || ( ( currentStartTimeAsLong <= time ) && ( ( invalidateAllData ) || ( time <= currentEndTimeAsLong ) ) ) )
            {
                startTimeIndicesToUpdate.add ( i );
            }
            else if ( time < currentStartTimeAsLong )
            {
                break;
            }
        }
    }

    /**
     * @see org.openscada.hd.Query#changeParameters
     */
    public void changeParameters ( final QueryParameters inputParameters )
    {
        receivingTask.submit ( new Runnable () {
            public void run ()
            {
                if ( ( inputParameters == null ) || ( inputParameters.getStartTimestamp () == null ) || ( inputParameters.getEndTimestamp () == null ) )
                {
                    close ();
                    return;
                }
                parameters = inputParameters;
                dataChanged = true;
                initialLoadPerformed = false;
                setQueryState ( QueryState.LOADING );
            }
        } );
    }

    /**
     * @see org.openscada.hd.Query#close
     */
    public void close ()
    {
        if ( !closed )
        {
            closed = true;
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
            if ( !updateData )
            {
                closerTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( QUERY_CLOSER_THREAD_ID ) );
                closerTask.submit ( new Runnable () {
                    public void run ()
                    {
                        service.removeQuery ( QueryImpl.this );
                    }
                } );
            }
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
            markTimeAsDirty ( longValue.getTime (), false );
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
            markTimeAsDirty ( doubleValue.getTime (), false );
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

    /**
     * This method invalidates all data up to the specified time.
     * @param time time up to which data has to be invalidated
     */
    public void updateDataBefore ( final long time )
    {
        markTimeAsDirty ( time, true );
    }

    /**
     * This method returns the lowest compression level that is sufficient for the specified detail level
     * @param timespan time span in milliseconds the requested compression level must provide at least in order to be considered as recommended compression level
     * @return recommended compression level
     */
    private long getRecommendedCompressionLevel ( final long timespan )
    {
        // optimize in case of minimal time span
        if ( timespan <= 1 )
        {
            return 0;
        }

        // search for optimal compression level
        long minimumAvailableCompressionLevel = 0;
        long providedTimespan = Long.MIN_VALUE;
        try
        {
            for ( final Entry<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> entry : storageChannels.entrySet () )
            {
                final long detailLevelId = entry.getKey ();
                if ( detailLevelId > 0 )
                {
                    final Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> map = entry.getValue ();
                    for ( final Map<ExtendedStorageChannel, CalculationLogicProvider> calculationMethodEntry : map.values () )
                    {
                        for ( final CalculationLogicProvider value : calculationMethodEntry.values () )
                        {
                            final long compressionTimespan = value.getRequiredTimespanForCalculation ();
                            if ( ( compressionTimespan < timespan ) && ( compressionTimespan > providedTimespan ) )
                            {
                                providedTimespan = compressionTimespan;
                                minimumAvailableCompressionLevel = detailLevelId;
                            }
                        }
                    }
                }
            }
        }
        catch ( final Exception e )
        {
            logger.error ( "problem while determining recommended compression level for query", e );
        }
        return minimumAvailableCompressionLevel;
    }

    /**
     * This method returns the currently available values for the given time span.
     * The returned map contains all available storage channels for the given level.
     * @param compressionLevel compression level for which data has to be retrieved
     * @param startTime start time of the requested data
     * @param endTime end time of the requested data
     * @return map containing all available storage channels for the given level
     * @throws Exception in case of problems retrieving the requested data
     */
    private Map<StorageChannelMetaData, BaseValue[]> getValues ( final long compressionLevel, final long startTime, final long endTime ) throws Exception
    {
        logger.debug ( "requested compression level: " + compressionLevel );
        final Map<StorageChannelMetaData, BaseValue[]> result = new HashMap<StorageChannelMetaData, BaseValue[]> ();
        try
        {
            final Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> map = storageChannels.get ( compressionLevel );
            for ( final Entry<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> subEntry : map.entrySet () )
            {
                for ( final Entry<ExtendedStorageChannel, CalculationLogicProvider> entry : subEntry.getValue ().entrySet () )
                {
                    final ExtendedStorageChannel storageChannel = entry.getKey ();
                    final StorageChannelMetaData metaData = storageChannel.getMetaData ();
                    final CalculationLogicProvider calculationLogicProvider = entry.getValue ();
                    BaseValue[] values = null;
                    switch ( calculationLogicProvider.getOutputType () )
                    {
                    case LONG_VALUE:
                    {
                        values = storageChannel.getLongValues ( startTime, endTime );
                        break;
                    }
                    case DOUBLE_VALUE:
                    {
                        values = storageChannel.getDoubleValues ( startTime, endTime );
                        break;
                    }
                    }
                    if ( compressionLevel == 0 )
                    {
                        // create a virtual entry for each required calculation method
                        for ( final CalculationMethod calculationMethod : calculationMethods )
                        {
                            final StorageChannelMetaData subMetaData = new StorageChannelMetaData ( metaData );
                            subMetaData.setCalculationMethod ( calculationMethod );
                            result.put ( subMetaData, values );
                        }
                    }
                    else
                    {
                        result.put ( metaData, values );
                    }
                }
            }
        }
        catch ( final Exception e )
        {
            final String message = "unable to retrieve values from storage channel";
            logger.error ( message, e );
            throw new Exception ( message, e );
        }
        return result;
    }

    /**
     * This method returns the latest NATIVE value or null, if no value is available at all.
     * @return latest NATIVE value or null, if no value is available at all
     */
    private BaseValue getLatestValue ()
    {
        final Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> map = storageChannels.get ( 0 );
        if ( map == null )
        {
            return null;
        }
        final Map<ExtendedStorageChannel, CalculationLogicProvider> map2 = map.get ( CalculationMethod.NATIVE );
        if ( map2 == null )
        {
            return null;
        }
        for ( final ExtendedStorageChannel storageChannel : map2.keySet () )
        {
            return StorageHistoricalItemService.getLatestValue ( storageChannel );
        }
        return null;
    }
}
