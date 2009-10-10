package org.openscada.hd.server.storage;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.openscada.hd.server.storage.calculation.CalculationLogicProvider;
import org.openscada.hd.server.storage.datatypes.BaseValue;
import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.datatypes.DoubleValue;
import org.openscada.hd.server.storage.datatypes.LongValue;
import org.openscada.hd.server.storage.utils.ValueArrayNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as base implementation for storage channel implementations providing calculation methods.
 * @author Ludwig Straub
 */
public class CalculatingStorageChannel extends SimpleStorageChannelManager
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( CalculatingStorageChannel.class );

    /** Storage channel that is used as main channel when writing results of calculations. */
    private final ExtendedStorageChannel baseStorageChannel;

    /** Storage channel that is used to request data if data is missing for instance after startup. */
    private final ExtendedStorageChannel inputStorageChannel;

    /** Logic provider for calculation of values for storage channel. */
    private final CalculationLogicProvider calculationLogicProvider;

    /** Timer that is used to calculate virtual values as soon as the next time span of data is passed. */
    private final Timer virtualValuesCalculationTimer;

    /** The start time of the latest processed time span. */
    private long latestProcessedTimeSpan;

    /**
     * Fully initializing constructor.
     * @param baseStorageChannel storage channel that is used as main channel when writing results of calculations
     * @param inputStorageChannel storage channel that is used to request data if data is missing for instance after startup
     * @param calculationLogicProvider logic provider for calculation of values for storage channel
     */
    public CalculatingStorageChannel ( final ExtendedStorageChannel baseStorageChannel, final ExtendedStorageChannel inputStorageChannel, final CalculationLogicProvider calculationLogicProvider )
    {
        // initialize data
        this.baseStorageChannel = baseStorageChannel;
        this.inputStorageChannel = inputStorageChannel;
        this.calculationLogicProvider = calculationLogicProvider;

        // calculate values of the past
        this.latestProcessedTimeSpan = getLastCalculatedValueTime ();
        final long currentTimeSpan = getTimeSpanStart ( System.currentTimeMillis () );
        try
        {
            calculateOldValues ( latestProcessedTimeSpan, currentTimeSpan );
            this.latestProcessedTimeSpan = currentTimeSpan;
        }
        catch ( Exception e )
        {
            // ignore exception
            // nothing more can be done here.
            // the system will continue to function
            logger.warn ( "could not retrieve old value while constructing instance!", e );
        }

        // start timer if virtual values have to be calculated
        if ( !calculationLogicProvider.getPassThroughValues () && calculationLogicProvider.getGenerateVirtualValues () )
        {
            virtualValuesCalculationTimer = new Timer ();
            virtualValuesCalculationTimer.scheduleAtFixedRate ( new TimerTask () {
                public void run ()
                {
                    try
                    {
                        notifyNewValues ( null );
                    }
                    catch ( Exception e )
                    {
                        // ignore exception
                        // nothing more can be done here.
                        // the system will continue to function
                        logger.warn ( "could not retrieve old value within update timer!", e );
                    }
                }
            }, currentTimeSpan, currentTimeSpan + calculationLogicProvider.getRequiredTimespanForCalculation () );
        }
        else
        {
            virtualValuesCalculationTimer = null;
        }
    }

    /**
     * This method returns an empty array of the data type that is requested.
     * @param dataType data type of the requested array
     * @return array of the data type that is requested
     */
    private BaseValue[] getEmptyArray ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY;
        }
        case DOUBLE_VALUE:
        {
            return ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
        }
        }
        return null;
    }

    /**
     * This method retrieves the values from the passed storage channel matching the specified time span.
     * @param storageChannel storage channel to be used
     * @param dataType data type that has to be retrieved
     * @param startTime start time of time span
     * @param endTime end time of time span
     * @return retrieved values
     */
    private BaseValue[] getValues ( final ExtendedStorageChannel storageChannel, final DataType dataType, final long startTime, final long endTime )
    {
        if ( storageChannel != null )
        {
            try
            {
                switch ( dataType )
                {
                case LONG_VALUE:
                {
                    return storageChannel.getLongValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE );
                }
                case DOUBLE_VALUE:
                {
                    return storageChannel.getDoubleValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE );
                }
                }
            }
            catch ( Exception e )
            {
                logger.warn ( "could not retrieve latest value!", e );
            }
        }
        return null;
    }

    /**
     * This method returns the time when the time span containing the passed time stamp that has to be processed by the calculation logic starts.
     * @param time time stamp within the time span
     * @return start of the time span
     */
    private long getTimeSpanStart ( final long time )
    {
        return time - time % calculationLogicProvider.getRequiredTimespanForCalculation ();
    }

    /**
     * This method returns the last value that was calculated and processed.
     * If no value was calculated until now, Long.MIN_VALUE will be returned.
     * @return time of last value that was calculated and processed or Long.MIN_VALUE if no calculation has been performed yet
     */
    private long getLastCalculatedValueTime ()
    {
        BaseValue value = null;
        if ( baseStorageChannel != null )
        {
            BaseValue[] values = getValues ( baseStorageChannel, calculationLogicProvider.getOutputType (), Long.MAX_VALUE - 1, Long.MAX_VALUE );
            value = ( values != null ) && ( values.length > 0 ) ? values[0] : null;

        }
        return value == null ? Long.MIN_VALUE : value.getTime ();
    }

    /**
     * This method calculates all values that should have been calculated in the past, but are not available in the storage channel.
     * This is the case if the system was shut down or if a new storage channel with a new calculation method has been added.
     * @param startTime start time of the time span for which a calculation has to be made
     * @param endTime end time of the time span for whcih a calculation has to be made
     * @throws Exception in case of any problems
     */
    private void calculateOldValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( inputStorageChannel != null )
        {
            final DataType inputType = calculationLogicProvider.getInputType ();
            final BaseValue[] values = getValues ( inputStorageChannel, inputType, startTime, endTime );
            if ( ( values != null ) && ( values.length > 0 ) )
            {
                processValues ( values, startTime, endTime );
            }
        }
    }

    /**
     * This method triggers the functionality that retrieves old values from the input storage channel and calculates values for the base storage channel.
     * The method should not be called outside of this package.
     * @param values values that have to be processed
     * @throws Exception in case of any problems
     */
    synchronized void notifyNewValues ( final BaseValue[] values ) throws Exception
    {
        // collect all timespan blocks that have to be updated
        final Set<Long> startTimes = new HashSet<Long> ();
        final long currentlyAvailableData = getTimeSpanStart ( System.currentTimeMillis () );

        // add blocks for which real values are available
        long maxStartTime = latestProcessedTimeSpan;
        if ( ( values != null ) && ( values.length > 0 ) )
        {
            for ( BaseValue value : values )
            {
                final long time = value.getTime ();
                if ( time < currentlyAvailableData )
                {
                    long startTime = getTimeSpanStart ( time );
                    startTimes.add ( startTime );
                    if ( startTime > maxStartTime )
                    {
                        maxStartTime = startTime;
                    }
                }
            }
        }

        // add blocks that have not yet been processed and blocks for virtual values if virtual values are required
        final long requiredTimespanForCalculation = calculationLogicProvider.getRequiredTimespanForCalculation ();
        final long previousAvailableData = calculationLogicProvider.getGenerateVirtualValues () ? currentlyAvailableData - requiredTimespanForCalculation : maxStartTime;
        while ( latestProcessedTimeSpan < previousAvailableData )
        {
            latestProcessedTimeSpan += requiredTimespanForCalculation;
            startTimes.add ( latestProcessedTimeSpan );
        }

        // process time spans
        final long timeSpan = calculationLogicProvider.getRequiredTimespanForCalculation ();
        for ( Long startTime : startTimes )
        {
            final long endTime = startTime + timeSpan;
            calculateOldValues ( startTime, endTime );
            if ( latestProcessedTimeSpan < endTime )
            {
                latestProcessedTimeSpan = endTime;
            }
        }
    }

    /**
     * This method forwards the passed values to the correct processing method.
     * @param values values that have to be processed
     * @param minStartTime mimimum start time of the time spans that will be processed
     * @param maxEndTime maximum end of the time spans that will be processed
     */
    private void processValues ( final BaseValue[] values, final long minStartTime, final long maxEndTime ) throws Exception
    {
        if ( ( values != null ) && ( values.length > 0 ) )
        {
            final BaseValue[] emptyArray = getEmptyArray ( calculationLogicProvider.getInputType () );
            final long blockMid = Math.max ( minStartTime, values[0].getTime () );
            final long blockTimeSpan = calculationLogicProvider.getRequiredTimespanForCalculation ();
            long blockStart = getTimeSpanStart ( blockMid );
            while ( blockStart < maxEndTime )
            {
                final long blockEnd = blockStart + blockTimeSpan;
                BaseValue[] valueBlock = ValueArrayNormalizer.extractSubArray ( values, blockStart, blockEnd, emptyArray );
                if ( valueBlock.length == 0 )
                {
                    break;
                }
                try
                {
                    switch ( calculationLogicProvider.getOutputType () )
                    {
                    case LONG_VALUE:
                    {
                        LongValue[] longValues = (LongValue[])calculationLogicProvider.generateValues ( valueBlock );
                        if ( baseStorageChannel != null )
                        {
                            baseStorageChannel.updateLongs ( longValues );
                        }
                        super.updateLongs ( longValues );
                    }
                    case DOUBLE_VALUE:
                    {
                        DoubleValue[] doubleValues = (DoubleValue[])calculationLogicProvider.generateValues ( valueBlock );
                        if ( baseStorageChannel != null )
                        {
                            baseStorageChannel.updateDoubles ( doubleValues );
                        }
                        super.updateDoubles ( doubleValues );
                    }
                    }
                }
                catch ( Exception e )
                {
                    String message = "could not process values!";
                    logger.error ( message, e );
                    throw new Exception ( message, e );
                }
                blockStart = blockEnd;
            }
        }
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        updateLongs ( new LongValue[] { longValue } );
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( calculationLogicProvider.getPassThroughValues () )
        {
            if ( baseStorageChannel != null )
            {
                baseStorageChannel.updateLongs ( longValues );
            }
            super.updateLongs ( longValues );
        }
        else
        {
            notifyNewValues ( longValues );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            return baseStorageChannel.getLongValues ( startTime, endTime );
        }
        return EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        updateDoubles ( new DoubleValue[] { doubleValue } );
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( calculationLogicProvider.getPassThroughValues () )
        {
            if ( baseStorageChannel != null )
            {
                baseStorageChannel.updateDoubles ( doubleValues );
            }
            super.updateDoubles ( doubleValues );
        }
        else
        {
            notifyNewValues ( doubleValues );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            return baseStorageChannel.getDoubleValues ( startTime, endTime );
        }
        return EMPTY_DOUBLEVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        notifyNewValues ( null );
        super.cleanupRelicts ();
        baseStorageChannel.cleanupRelicts ();
    }
}
