package org.openscada.hd.server.storage;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.Map.Entry;

import org.openscada.ca.Configuration;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
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
     * @param maximumCompressionLevel
     * @param startTime
     * @param endTime
     * @return
     */
    public synchronized Map<StorageChannelMetaData, BaseValue[]> getValues ( final long maximumCompressionLevel, final long startTime, final long endTime ) throws Exception
    {
        long maximumAvailableCompressionLevel = Long.MIN_VALUE;
        for ( StorageChannelMetaData metaData : storageChannels.keySet () )
        {
            maximumAvailableCompressionLevel = Math.max ( maximumAvailableCompressionLevel, metaData.getDetailLevelId () );
        }
        maximumAvailableCompressionLevel = Math.min ( maximumAvailableCompressionLevel, maximumCompressionLevel );
        Map<StorageChannelMetaData, BaseValue[]> result = new HashMap<StorageChannelMetaData, BaseValue[]> ();
        try
        {
            for ( Entry<StorageChannelMetaData, ExtendedStorageChannel> entry : storageChannels.entrySet () )
            {
                StorageChannelMetaData metaData = entry.getKey ();
                if ( metaData.getDetailLevelId () == maximumAvailableCompressionLevel )
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
            final Map<String, Value[]> map = new HashMap<String, Value[]> ();
            ValueInformation[] valueInformations = null;
            final Set<String> calculationMethods = new HashSet<String> ();
            for ( final Entry<StorageChannelMetaData, ExtendedStorageChannel> entry : this.storageChannels.entrySet () )
            {
                final CalculationMethod calculationMethod = entry.getKey ().getCalculationMethod ();
                final DoubleValue[] dvs = entry.getValue ().getDoubleValues ( parameters.getStartTimestamp ().getTimeInMillis (), parameters.getEndTimestamp ().getTimeInMillis () );
                final Value[] values = new Value[dvs.length];
                if ( calculationMethod == CalculationMethod.NATIVE )
                {
                    valueInformations = new ValueInformation[dvs.length];
                }
                for ( int i = 0; i < dvs.length; i++ )
                {
                    final DoubleValue doubleValue = dvs[i];
                    values[i] = new Value ( doubleValue.getValue () );
                    if ( calculationMethod == CalculationMethod.NATIVE )
                    {
                        valueInformations[i] = new ValueInformation ( parameters.getStartTimestamp (), parameters.getEndTimestamp (), doubleValue.getQualityIndicator (), doubleValue.getBaseValueCount () );
                    }
                }
                if ( calculationMethod == CalculationMethod.NATIVE )
                {
                    map.put ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), values );
                    calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ) );
                }
            }
            listener.updateParameters ( parameters, calculationMethods );
            listener.updateData ( 0, map, valueInformations );
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
        if ( !this.started || this.rootStorageChannel == null || value == null )
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
            DataType processedDataType = DataType.UNKNOWN;
            if ( variant.isLong () || variant.isInteger () || variant.isBoolean () )
            {
                LongValue longValue = new LongValue ( time, qualityIndicator, 1, variant.asLong ( 0L ) );
                this.rootStorageChannel.updateLong ( longValue );
                for ( QueryImpl query : this.openQueries )
                {
                    query.updateLong ( longValue );
                }
                processedDataType = DataType.LONG_VALUE;
            }
            else
            {
                DoubleValue doubleValue = new DoubleValue ( time, qualityIndicator, 1, variant.asDouble ( 0.0 ) );
                this.rootStorageChannel.updateDouble ( doubleValue );
                for ( QueryImpl query : this.openQueries )
                {
                    query.updateDouble ( doubleValue );
                }
                processedDataType = DataType.DOUBLE_VALUE;
            }
            if ( expectedDataType != processedDataType )
            {
                logger.warn ( String.format ( "processed data type (%s) does not match expected data type (%s)!", processedDataType, expectedDataType ) );
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
     */
    public synchronized void stop ()
    {
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
