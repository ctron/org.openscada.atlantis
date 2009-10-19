package org.openscada.hd.server.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.Configuration;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hd.server.storage.internal.Conversions;
import org.openscada.hd.server.storage.internal.QueryImpl;
import org.openscada.hd.server.storage.internal.StorageThreadFactory;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of StorageHistoricalItem as OSGi service.
 * @see org.openscada.hd.server.common.StorageHistoricalItem
 * @author Ludwig Straub
 */
public class ShiService implements StorageHistoricalItem, Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( ShiService.class );

    /** Delay in milliseconds after that old data is deleted for the first time after initialization of the class. */
    private final static long CLEANER_TASK_DELAY = 1000 * 1;

    /** Period in milliseconds between two consecutive attempts to delete old data. */
    private final static long CLEANER_TASK_PERIOD = 1000 * 10;

    /** Configuration of the service. */
    private final Configuration configuration;

    /** Set of all calculation methods except NATIVE. */
    private final Set<CalculationMethod> calculationMethods;

    /** All available storage channels mapped via calculation method. */
    private final Map<StorageChannelMetaData, ExtendedStorageChannel> storageChannels;

    /** Reference to the main input storage channel that is also available in the storage channels map. */
    private ExtendedStorageChannel rootStorageChannel;

    /** Flag indicating whether the service is currently running or not. */
    private boolean started;

    /** Task that is used for deleting old data. */
    private ScheduledThreadPoolExecutor relictCleanerTask;

    /** List of currently open queries. */
    private final Collection<QueryImpl> openQueries;

    /** Expected input data type. */
    private DataType expectedDataType;

    /** Latest time of known valid information. */
    private final long latestReliableTime;

    /** Flag indicating whether old data should be deleted or not. */
    private final boolean importMode;

    /** Maximum age of data that will be processed by the service if not running in import mode. */
    private final long proposedDataAge;

    /**
     * Constructor.
     * @param configuration configuration of the service
     * @param latestReliableTime latest time of known valid information
     * @param importMode flag indicating whether old data should be deleted or not
     */
    public ShiService ( final Configuration configuration, final long latestReliableTime, final boolean importMode )
    {
        this.configuration = new ConfigurationImpl ( configuration );
        calculationMethods = new HashSet ( Conversions.getCalculationMethods ( configuration ) );
        this.storageChannels = new HashMap<StorageChannelMetaData, ExtendedStorageChannel> ();
        this.rootStorageChannel = null;
        this.started = false;
        this.openQueries = new LinkedList<QueryImpl> ();
        expectedDataType = DataType.UNKNOWN;
        this.latestReliableTime = latestReliableTime;
        this.importMode = importMode;
        this.proposedDataAge = Conversions.decodeTimeSpan ( configuration.getData ().get ( Conversions.PROPOSED_DATA_AGE_KEY_PREFIX + 0 ) );
    }

    /**
     * This method adds a query to the collection of currently open queries.
     * @param query query to be added
     */
    public synchronized void addQuery ( final QueryImpl query )
    {
        openQueries.add ( query );
    }

    /**
     * This method removes a query from the collection of currently open queries.
     * If the query is not found in the collection then no action is performed.
     * @param query query to be removed
     */
    public synchronized void removeQuery ( final QueryImpl query )
    {
        openQueries.remove ( query );
    }

    /**
     * This method returns the maximum available compression level of all storage channels.
     * @return maximum available compression level of all storage channels or negative value if no storage channel is availavle
     */
    public synchronized long getMaximumCompressionLevel ()
    {
        long maximumAvailableCompressionLevel = Long.MIN_VALUE;
        for ( final StorageChannelMetaData metaData : storageChannels.keySet () )
        {
            maximumAvailableCompressionLevel = Math.max ( maximumAvailableCompressionLevel, metaData.getDetailLevelId () );
        }
        return maximumAvailableCompressionLevel;
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
    public synchronized Map<StorageChannelMetaData, BaseValue[]> getValues ( final long compressionLevel, final long startTime, final long endTime ) throws Exception
    {
        final Map<StorageChannelMetaData, BaseValue[]> result = new HashMap<StorageChannelMetaData, BaseValue[]> ();
        try
        {
            for ( final Entry<StorageChannelMetaData, ExtendedStorageChannel> entry : storageChannels.entrySet () )
            {
                final StorageChannelMetaData metaData = entry.getKey ();
                if ( metaData.getDetailLevelId () == compressionLevel )
                {
                    final ExtendedStorageChannel storageChannel = entry.getValue ();
                    BaseValue[] values = null;
                    switch ( expectedDataType )
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
                        result.put ( storageChannel.getMetaData (), values );
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
     * This method returns a reference to the current configuration of the service.
     * @return reference to the current configuration of the service
     */
    public synchronized Configuration getConfiguration ()
    {
        return new ConfigurationImpl ( configuration );
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#createQuery
     */
    public synchronized Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        try
        {
            return new QueryImpl ( this, listener, parameters, calculationMethods, updateData );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create query", e );
        }
        return null;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#getInformation
     */
    public synchronized HistoricalItemInformation getInformation ()
    {
        return Conversions.convertConfigurationToHistoricalItemInformation ( configuration );
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#updateData
     */
    public synchronized void updateData ( final DataItemValue value )
    {
        if ( !this.started || ( this.rootStorageChannel == null ) || ( value == null ) )
        {
            return;
        }
        final Variant variant = value.getValue ();
        if ( variant == null )
        {
            return;
        }
        final Calendar calendar = value.getTimestamp ();
        final long now = System.currentTimeMillis ();
        final long time = calendar == null ? now : calendar.getTimeInMillis ();
        if ( importMode || ( ( now - proposedDataAge ) > time ) )
        {
            logger.warn ( "data that is too old for being processed was received! data will be ignored: (configuration: '%s'; time: %s)", configuration.getId (), time );
        }
        final double qualityIndicator = !value.isConnected () || value.isError () ? 0 : 1;
        try
        {
            if ( expectedDataType == DataType.LONG_VALUE )
            {
                final LongValue longValue = new LongValue ( time, qualityIndicator, 1, variant.asLong ( 0L ) );
                this.rootStorageChannel.updateLong ( longValue );
                for ( final QueryImpl query : this.openQueries )
                {
                    query.updateLong ( longValue );
                }
            }
            else
            {
                final DoubleValue doubleValue = new DoubleValue ( time, qualityIndicator, 1, variant.asDouble ( 0.0 ) );
                this.rootStorageChannel.updateDouble ( doubleValue );
                for ( final QueryImpl query : this.openQueries )
                {
                    query.updateDouble ( doubleValue );
                }
            }
            final DataType receivedDataType = variant.isBoolean () || variant.isInteger () || variant.isLong () ? DataType.LONG_VALUE : DataType.DOUBLE_VALUE;
            if ( !variant.isNull () && ( expectedDataType != receivedDataType ) )
            {
                logger.warn ( String.format ( "received data type (%s) does not match expected data type (%s)!", receivedDataType, expectedDataType ) );
            }
        }
        catch ( final Exception e )
        {
            logger.error ( String.format ( "could not process value (%s)", variant ), e );
        }
    }

    /**
     * This method adds a storage channel.
     * @param storageChannel storage channel that has to be added
     */
    public synchronized void addStorageChannel ( final ExtendedStorageChannel storageChannel )
    {
        if ( storageChannel != null )
        {
            try
            {
                final StorageChannelMetaData metaData = storageChannel.getMetaData ();
                if ( metaData != null )
                {
                    storageChannels.put ( metaData, storageChannel );
                    if ( metaData.getCalculationMethod () == CalculationMethod.NATIVE )
                    {
                        rootStorageChannel = storageChannel;
                        expectedDataType = metaData.getDataType ();
                    }
                }
            }
            catch ( final Exception e )
            {
                logger.error ( "could not retrieve meta data information of storage channel", e );
            }
        }
    }

    /**
     * This method creates an invalid entry using the data of the latest existing entry.
     * No entry is made if no data at all is available in the root storage channel.
     * @param time time of the entry that has to be created
     */
    private void createInvalidEntry ( final long time )
    {
        if ( rootStorageChannel != null )
        {
            BaseValue[] values = null;
            try
            {
                if ( expectedDataType == DataType.LONG_VALUE )
                {
                    values = rootStorageChannel.getLongValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE );
                }
                else
                {
                    values = rootStorageChannel.getDoubleValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE );
                }
            }
            catch ( final Exception e )
            {
                logger.error ( "could not retrieve latest value from root storage channel", e );
            }
            if ( ( values != null ) && ( values.length > 0 ) )
            {
                final BaseValue value = values[values.length - 1];
                value.setTime ( Math.max ( time, value.getTime () ) );
                value.setQualityIndicator ( 0 );
                value.setBaseValueCount ( 0 );
                try
                {
                    if ( expectedDataType == DataType.LONG_VALUE )
                    {
                        rootStorageChannel.updateLong ( (LongValue)value );
                    }
                    else
                    {
                        rootStorageChannel.updateDouble ( (DoubleValue)value );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( "could not store value via root storage channel", e );
                }
            }
        }
    }

    /**
     * This method activates the service processing.
     * The methods updateData and createQuery only have effect after calling this method.
     */
    public synchronized void start ()
    {
        stop ();
        this.started = true;
        createInvalidEntry ( latestReliableTime );
        if ( !importMode )
        {
            this.relictCleanerTask = new ScheduledThreadPoolExecutor ( 1, StorageThreadFactory.createFactory ( "RelictCleaner" ) );
            this.relictCleanerTask.setMaximumPoolSize ( 1 );
            this.relictCleanerTask.scheduleWithFixedDelay ( this, CLEANER_TASK_DELAY, CLEANER_TASK_PERIOD, TimeUnit.MILLISECONDS );
        }
    }

    /**
     * This method stops the service from processing and destroys its internal structure.
     * The service cannot be started again, after stop has been called.
     * After calling this method, no further call to this service can be made.
     * Before the service is stopped, an invalid value is processed in order to mark
     * future values as invalid until a valid value is processed again.
     */
    public synchronized void stop ()
    {
        // close existing queries
        for ( final QueryImpl query : new ArrayList<QueryImpl> ( openQueries ) )
        {
            query.close ();
        }

        // stop relict cleaner task
        if ( relictCleanerTask != null )
        {
            relictCleanerTask.shutdown ();
            relictCleanerTask = null;
        }

        // create entry with data marked as invalid
        if ( started )
        {
            createInvalidEntry ( System.currentTimeMillis () );
        }

        // set running flag
        started = false;
    }

    /**
     * This method cleans old data.
     * @see org.openscada.hsdb.relict.RelictCleaner#cleanupRelicts
     */
    public synchronized void run ()
    {
        if ( started && ( rootStorageChannel != null ) )
        {
            try
            {
                rootStorageChannel.cleanupRelicts ();
            }
            catch ( final Exception e )
            {
                logger.error ( "could not clean old data", e );
            }
        }
    }
}
