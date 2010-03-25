package org.openscada.hd.server.storage;

import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hd.server.storage.internal.QueryImpl;
import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.backend.AbortNotificator;
import org.openscada.hsdb.backend.BackEndManager;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.concurrent.HsdbThreadFactory;
import org.openscada.hsdb.configuration.Conversions;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.relict.RelictCleaner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of StorageHistoricalItem as OSGi service.
 * @see org.openscada.hd.server.common.StorageHistoricalItem
 * @author Ludwig Straub
 */
public class StorageHistoricalItemService implements StorageHistoricalItem, RelictCleaner
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( StorageHistoricalItemService.class );

    /** Id prefix of the startup thread. */
    private final static String STARTUP_THREAD_ID_PREFIX = "hd.StorageHistoricalItemServiceStartup_";

    /** Id prefix of the query creator thread. */
    private final static String CREATE_QUERY_THREAD_ID_PREFIX = "hd.CreateQuery_";

    /** Id prefix of the data receiver thread. */
    private final static String DATA_RECEIVER_THREAD_ID_PREFIX = "hd.StorageHistoricalItemServiceDataReceiver_";

    /** Manager that will be used to handle and distribute back end objects. */
    private final BackEndManager<?> backEndManager;

    /** Set of all calculation methods except NATIVE. */
    private final CalculationMethod[] calculationMethods;

    /** Reference to the main input storage channel that is also available in the storage channels map. */
    private CalculatingStorageChannel rootStorageChannel;

    /** Flag indicating whether the service is currently starting or not. */
    private volatile boolean starting;

    /** Flag indicating whether the service is currently running or not. */
    private volatile boolean started;

    /** List of currently open queries. */
    private final Collection<QueryImpl> openQueries;

    /** Expected input data type. */
    private final DataType expectedDataType;

    /** Latest time of known valid information. */
    private final long latestReliableTime;

    /** Flag indicating whether old data should be deleted or not. */
    private final boolean importMode;

    /** Maximum age of data that will be processed by the service if not running in import mode. */
    private final long proposedDataAge;

    /** Maximum time in milliseconds the new value can differ from the current time in order to be processed. */
    private final long acceptedTimeDelta;

    /** Registration of this service. */
    private ServiceRegistration registration;

    /** Task that receives the incoming data. */
    private ExecutorService dataReceiver;

    /** Task that performs the startup of the service. */
    private ExecutorService startUpTask;

    /** Task that performs the startup operation for a new query. */
    private ExecutorService createQueryTask;

    /** Latest processed value. */
    private BaseValue latestProcessedValue;

    /** Object that is used for internal synchronization. */
    private final Object lockObject;

    /**
     * Constructor.
     * @param backEndManager manager that will be used to handle and distribute back end objects
     * @param latestReliableTime latest time of known valid information
     * @param importMode flag indicating whether old data should be deleted or not
     */
    public StorageHistoricalItemService ( final BackEndManager<?> backEndManager, final long latestReliableTime, final boolean importMode )
    {
        this.backEndManager = backEndManager;
        final org.openscada.hsdb.configuration.Configuration configuration = backEndManager.getConfiguration ();
        calculationMethods = Conversions.getCalculationMethods ( configuration );
        rootStorageChannel = null;
        lockObject = new Object ();
        starting = false;
        started = false;
        openQueries = new CopyOnWriteArrayList<QueryImpl> ();
        this.latestReliableTime = latestReliableTime;
        this.importMode = importMode;
        final Map<String, String> data = configuration.getData ();
        proposedDataAge = Conversions.decodeTimeSpan ( data.get ( ConfigurationImpl.PROPOSED_DATA_AGE_KEY_PREFIX + 0 ) );
        acceptedTimeDelta = Conversions.decodeTimeSpan ( data.get ( ConfigurationImpl.ACCEPTED_TIME_DELTA_KEY ) );
        expectedDataType = DataType.convertShortStringToDataType ( data.get ( ConfigurationImpl.DATA_TYPE_KEY ) );
        registration = null;
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
     * This method returns the latest value via the passed storage channel or null, if no value is available at all.
     * @param storageChannel storage channel that has to be used for retrieving the latest value
     * @return latest value or null, if no value is available at all
     */
    public static BaseValue getLatestValue ( final ExtendedStorageChannel storageChannel )
    {
        if ( storageChannel != null )
        {
            try
            {
                final BaseValue[] result = storageChannel.getMetaData ().getDataType () == DataType.LONG_VALUE ? storageChannel.getLongValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE ) : storageChannel.getDoubleValues ( Long.MAX_VALUE - 1, Long.MAX_VALUE );
                return ( result != null ) && ( result.length > 0 ) ? result[0] : null;
            }
            catch ( final Exception e )
            {
                logger.error ( "unable to retriebe latest value from root storage channel", e );
            }
        }
        return null;
    }

    /**
     * This method returns the latest NATIVE value or null, if no value is available at all.
     * @return latest NATIVE value or null, if no value is available at all
     */
    public BaseValue getLatestValue ()
    {
        return getLatestValue ( rootStorageChannel );
    }

    /**
     * This method returns a reference to the current back end manager of the service.
     * @return reference to the current back end manager of the service
     */
    public synchronized BackEndManager<?> getBackEndManager ()
    {
        return backEndManager;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#createQuery
     */
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        try
        {
            final QueryImpl query = new QueryImpl ( this, listener, parameters, calculationMethods );
            final StorageHistoricalItemService service = this;
            createQueryTask.submit ( new Runnable () {
                public void run ()
                {
                    query.run ( backEndManager.buildStorageChannelStructure (), updateData );
                    if ( updateData )
                    {
                        service.addQuery ( query );
                    }
                }
            } );
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
    public synchronized HistoricalItemInformation getInformation ()
    {
        // prepare data for result
        final org.openscada.hsdb.configuration.Configuration configuration = backEndManager.getConfiguration ();
        final Map<String, Variant> variantData = new HashMap<String, Variant> ();

        // process data
        final Map<String, String> data = configuration.getData ();
        if ( data != null )
        {
            for ( final Entry<String, String> entry : data.entrySet () )
            {
                variantData.put ( entry.getKey (), new Variant ( entry.getValue () ) );
            }
        }
        return new HistoricalItemInformation ( configuration.getId (), variantData );
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#updateData
     */
    public void updateData ( final DataItemValue value )
    {
        final long now = System.currentTimeMillis ();
        logger.debug ( "receiving data at: " + now );
        if ( dataReceiver != null )
        {
            dataReceiver.submit ( new Runnable () {
                public void run ()
                {
                    // check if input is valid
                    logger.debug ( "processing data after: " + ( System.currentTimeMillis () - now ) );
                    if ( value == null )
                    {
                        createInvalidEntry ( now );
                        return;
                    }
                    final Variant variant = value.getValue ();
                    if ( variant == null )
                    {
                        createInvalidEntry ( now );
                        return;
                    }
                    final Calendar calendar = value.getTimestamp ();
                    final long time = calendar == null ? now : calendar.getTimeInMillis ();
                    if ( !importMode && ( ( ( now - acceptedTimeDelta ) > time ) || ( ( now - proposedDataAge ) > time ) ) )
                    {
                        logger.warn ( String.format ( "data that is too old for being processed was received! data will be ignored: (configuration: '%s'; time: %s)", backEndManager.getConfiguration ().getId (), time ) );
                        return;
                    }
                    if ( ( now + acceptedTimeDelta ) < time )
                    {
                        logger.warn ( String.format ( "timestamp of data is located too far in the future! data will be ignored: (configuration: '%s'; time: %s)", backEndManager.getConfiguration ().getId (), time ) );
                        return;
                    }

                    // process data
                    final double qualityIndicator = !value.isConnected () || value.isError () || variant.isNull () || ( ( expectedDataType == DataType.LONG_VALUE ) && ( variant.asLong ( null ) == null ) ) || ( ( expectedDataType == DataType.DOUBLE_VALUE ) && ( variant.asDouble ( null ) == null ) ) ? 0 : 1;
                    final double isManual = value.isManual () ? 1 : 0;
                    if ( expectedDataType == DataType.LONG_VALUE )
                    {
                        processData ( new LongValue ( time, qualityIndicator, isManual, 1, variant.asLong ( 0L ) ) );
                    }
                    else
                    {
                        processData ( new DoubleValue ( time, qualityIndicator, isManual, 1, variant.asDouble ( 0.0 ) ) );
                    }
                    logger.debug ( "data processing time: " + ( System.currentTimeMillis () - now ) );

                    // check processed data type and give warning if type does not match the expected type
                    final DataType receivedDataType = variant.isBoolean () || variant.isInteger () || variant.isLong () ? DataType.LONG_VALUE : DataType.DOUBLE_VALUE;
                    if ( !variant.isNull () && ( expectedDataType != receivedDataType ) )
                    {
                        logger.warn ( String.format ( "received data type (%s) does not match expected data type (%s) for configuration with id '%s'!", receivedDataType, expectedDataType, backEndManager.getConfiguration ().getId () ) );
                    }
                }
            } );
        }
    }

    /**
     * This method processes the data tha is received via the data receiver task.
     * @param value data that has to be processed
     */
    public synchronized void processData ( final BaseValue value )
    {
        if ( !started || ( rootStorageChannel == null ) || ( value == null ) )
        {
            return;
        }
        if ( latestProcessedValue == null )
        {
            latestProcessedValue = getLatestValue ();
        }
        if ( latestProcessedValue == null )
        {
            latestProcessedValue = value;
        }
        if ( value.getTime () < latestProcessedValue.getTime () )
        {
            logger.warn ( String.format ( "older value for configuration '%s' received than latest available value", backEndManager.getConfiguration ().getId () ) );
        }
        try
        {
            // process data
            if ( value instanceof LongValue )
            {
                final LongValue longValue = (LongValue)value;
                rootStorageChannel.updateLong ( longValue );
                for ( final QueryImpl query : openQueries )
                {
                    query.updateLong ( longValue );
                }
            }
            else
            {
                final DoubleValue doubleValue = (DoubleValue)value;
                rootStorageChannel.updateDouble ( doubleValue );
                for ( final QueryImpl query : openQueries )
                {
                    query.updateDouble ( doubleValue );
                }
            }

            // when importing data, the values are most likely strongly differing from each other in time.
            // this causes additional files to be generated that can be cleaned to increase performance
            if ( importMode )
            {
                cleanupRelicts ();
            }
        }
        catch ( final Exception e )
        {
            logger.error ( "could not process value", e );
        }
    }

    /**
     * This method creates an invalid marker entry using the data of the latest existing entry.
     * No entry is made if no data at all is available in the root storage channel.
     * It is assured that the time of the new entry is after the latest existing entry.
     * If necessary, the passed time will be increased to fit this requirement.
     * @param time time of the entry that has to be created
     */
    private synchronized void createInvalidEntry ( final long time )
    {
        if ( rootStorageChannel != null )
        {
            BaseValue[] values = null;
            try
            {
                if ( rootStorageChannel.getCalculationLogicProvider ().getOutputType () == DataType.LONG_VALUE )
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
                try
                {
                    if ( value instanceof LongValue )
                    {
                        rootStorageChannel.updateLong ( new LongValue ( Math.max ( value.getTime () + 1, time ), 0, 0, 0, ( (LongValue)value ).getValue () ) );
                    }
                    else
                    {
                        rootStorageChannel.updateDouble ( new DoubleValue ( Math.max ( value.getTime () + 1, time ), 0, 0, 0, ( (DoubleValue)value ).getValue () ) );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( "could not write value with quality=0", e );
                }
            }
        }
    }

    /**
     * This method activates the service processing and registers the service as OSGi service.
     * The methods updateData and createQuery only have effect after calling this method.
     * @param bundleContext OSGi bundle context
     */
    public synchronized void start ( final BundleContext bundleContext )
    {
        starting = true;
        started = true;
        final String configurationId = backEndManager.getConfiguration ().getId ();
        dataReceiver = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( DATA_RECEIVER_THREAD_ID_PREFIX + configurationId ) );
        startUpTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( STARTUP_THREAD_ID_PREFIX + configurationId ) );
        createQueryTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( CREATE_QUERY_THREAD_ID_PREFIX + configurationId ) );
        startUpTask.submit ( new Runnable () {
            public void run ()
            {
                startUpTask.shutdown ();
                backEndManager.repairBackEndFragmentsIfRequired ( new AbortNotificator () {
                    public boolean getAbort ()
                    {
                        synchronized ( lockObject )
                        {
                            return !starting || !started;
                        }
                    }
                } );
                boolean proceed = true;
                synchronized ( lockObject )
                {
                    proceed = starting;
                }
                if ( !proceed )
                {
                    stop ();
                    return;
                }
                rootStorageChannel = backEndManager.buildStorageChannelTree ();
                synchronized ( lockObject )
                {
                    proceed = starting;
                }
                if ( !proceed )
                {
                    stop ();
                    return;
                }
                if ( !importMode )
                {
                    createInvalidEntry ( latestReliableTime );
                }
                registerService ( bundleContext );
                synchronized ( lockObject )
                {
                    proceed = starting;
                    starting = false;
                }
                if ( !proceed )
                {
                    stop ();
                    return;
                }
            }
        } );
    }

    /**
     * This method registers the service via OSGi.
     * @param bundleContext OSGi bundle context
     */
    private void registerService ( final BundleContext bundleContext )
    {
        unregisterService ();
        final Dictionary<String, String> serviceProperties = new Hashtable<String, String> ();
        serviceProperties.put ( Constants.SERVICE_PID, backEndManager.getConfiguration ().getId () );
        serviceProperties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        serviceProperties.put ( Constants.SERVICE_DESCRIPTION, "A OpenSCADA Storage Historical Item Implementation" );
        registration = bundleContext.registerService ( new String[] { StorageHistoricalItemService.class.getName (), StorageHistoricalItem.class.getName () }, this, serviceProperties );
    }

    /**
     * This method unregisters a previously registered service.
     */
    private void unregisterService ()
    {
        if ( registration != null )
        {
            registration.unregister ();
            registration = null;
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
        // abort if service is not yet started
        if ( starting )
        {
            synchronized ( lockObject )
            {
                if ( starting )
                {
                    starting = false;
                    return;
                }
            }
        }

        // close existing queries
        for ( final QueryImpl query : openQueries )
        {
            query.close ();
        }

        // unregister service
        unregisterService ();

        // stop data receiver
        if ( dataReceiver != null )
        {
            dataReceiver.shutdown ();
            dataReceiver = null;
        }

        // stop startup task
        if ( startUpTask != null )
        {
            startUpTask.shutdown ();
            startUpTask = null;
        }

        // stop query startup task but keep it available to avoid synchronization issues
        if ( createQueryTask != null )
        {
            createQueryTask.shutdown ();
        }

        // create entry with data marked as invalid
        if ( started && !importMode )
        {
            createInvalidEntry ( System.currentTimeMillis () );
        }

        // deinitialize back end manager
        try
        {
            backEndManager.deinitialize ();
        }
        catch ( final Exception e )
        {
            logger.error ( String.format ( "unable to deinitialize back end manager for configuration with id '%s'", backEndManager.getConfiguration ().getId () ), e );
        }

        // set running flag
        started = false;
    }

    /**
     * This method cleans old data.
     * @see org.openscada.hsdb.relict.RelictCleaner#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        if ( !starting && started )
        {
            try
            {
                // clean data
                logger.info ( "cleaning old data" );
                if ( rootStorageChannel != null )
                {
                    rootStorageChannel.cleanupRelicts ();
                }

                // notify queries
                for ( final QueryImpl query : openQueries )
                {
                    query.updateDataBefore ( System.currentTimeMillis () - proposedDataAge );
                }
            }
            catch ( final Exception e )
            {
                logger.error ( "could not clean old data", e );
            }
        }
    }
}
