package org.openscada.hd.server.storage;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;

import org.openscada.ca.Configuration;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hd.server.storage.internal.QueryImpl;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.relict.RelictCleaner;
import org.openscada.hsdb.relict.RelictCleanerCallerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of StorageHistoricalItem as OSGi service.
 * @see org.openscada.hd.server.common.StorageHistoricalItem
 * @author Ludwig Straub
 */
public class ShiService implements StorageHistoricalItem, RelictCleaner
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( ShiService.class );

    /** Delay in milliseconds after that old data is deleted for the first time after initialization of the class. */
    private final static long CLEANER_TASK_DELAY = 1000 * 60;

    /** Period in milliseconds between two consecutive attempts to delete old data. */
    private final static long CLEANER_TASK_PERIOD = 1000 * 60 * 10;

    /** Configuration of the service. */
    private final Configuration configuration;

    /** All available storage channels mapped via calculation method. */
    private final Map<StorageChannelMetaData, ExtendedStorageChannel> storageChannels;

    /** Reference to the main input storage channel that is also available in the storage channels map. */
    private ExtendedStorageChannel rootStorageChannel;

    /** Flag indicating whether the service is currently running or not. */
    private boolean started;

    /** Timer that is used for deleting old data. */
    private Timer deleteRelictsTimer;

    /** List of currently open queries. */
    private Collection<QueryImpl> openQueries;

    /** Expected input data type. */
    private DataType expectedDataType;

    /**
     * Constructor
     * @param configuration configuration of the service
     */
    public ShiService ( final Configuration configuration )
    {
        this.configuration = new ConfigurationImpl ( configuration );
        this.storageChannels = new HashMap<StorageChannelMetaData, ExtendedStorageChannel> ();
        this.rootStorageChannel = null;
        this.started = false;
        this.openQueries = new LinkedList<QueryImpl> ();
        expectedDataType = DataType.UNKNOWN;
    }

    /**
     * This method adds a query to the collection of currently open queries.
     * @param query query to be added
     */
    private synchronized void addQuery ( final QueryImpl query )
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
        for ( StorageChannelMetaData metaData : storageChannels.keySet () )
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
        Map<StorageChannelMetaData, BaseValue[]> result = new HashMap<StorageChannelMetaData, BaseValue[]> ();
        try
        {
            for ( Entry<StorageChannelMetaData, ExtendedStorageChannel> entry : storageChannels.entrySet () )
            {
                StorageChannelMetaData metaData = entry.getKey ();
                if ( metaData.getDetailLevelId () == compressionLevel )
                {
                    ExtendedStorageChannel storageChannel = entry.getValue ();
                    switch ( expectedDataType )
                    {
                    case LONG_VALUE:
                    {
                        result.put ( storageChannel.getMetaData (), storageChannel.getLongValues ( startTime, endTime ) );
                        break;
                    }
                    case DOUBLE_VALUE:
                    {
                        result.put ( storageChannel.getMetaData (), storageChannel.getDoubleValues ( startTime, endTime ) );
                        break;
                    }
                    }
                }
            }
        }
        catch ( Exception e )
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
            final QueryImpl query = new QueryImpl ( this, listener, parameters, updateData );
            addQuery ( query );
            return query;
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
    public HistoricalItemInformation getInformation ()
    {
        // FIXME: remove the whole method
        return null;
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
        final long time = calendar == null ? System.currentTimeMillis () : calendar.getTimeInMillis ();
        final double qualityIndicator = !value.isConnected () || value.isError () ? 0 : 1;
        try
        {
            if ( expectedDataType == DataType.LONG_VALUE )
            {
                LongValue longValue = new LongValue ( time, qualityIndicator, 1, variant.asLong ( 0L ) );
                this.rootStorageChannel.updateLong ( longValue );
                for ( QueryImpl query : this.openQueries )
                {
                    query.updateLong ( longValue );
                }
            }
            else
            {
                DoubleValue doubleValue = new DoubleValue ( time, qualityIndicator, 1, variant.asDouble ( 0.0 ) );
                this.rootStorageChannel.updateDouble ( doubleValue );
                for ( QueryImpl query : this.openQueries )
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
                StorageChannelMetaData metaData = storageChannel.getMetaData ();
                if ( metaData != null )
                {
                    this.storageChannels.put ( metaData, storageChannel );
                    if ( metaData.getCalculationMethod () == CalculationMethod.NATIVE )
                    {
                        this.rootStorageChannel = storageChannel;
                        expectedDataType = metaData.getDataType ();
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error ( "could not retrieve meta data information of storage channel", e );
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
        this.deleteRelictsTimer = new Timer ();
        this.deleteRelictsTimer.schedule ( new RelictCleanerCallerTask ( this ), CLEANER_TASK_DELAY, CLEANER_TASK_PERIOD );
        this.started = true;
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
        // send invalid value to mark the future as not reliable
        updateData ( new DataItemValue () );

        // set running flag
        this.started = false;

        // stop relict cleaner timer
        if ( this.deleteRelictsTimer != null )
        {
            this.deleteRelictsTimer.cancel ();
            this.deleteRelictsTimer.purge ();
            this.deleteRelictsTimer = null;
        }

        // close existing queries
        for ( QueryImpl query : Collections.unmodifiableCollection ( openQueries ) )
        {
            query.close ();
        }
    }

    /**
     * @see org.openscada.hsdb.relict.RelictCleaner#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        if ( rootStorageChannel != null )
        {
            rootStorageChannel.cleanupRelicts ();
        }
    }
}
