package org.openscada.hd.server.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEndManager;
import org.openscada.hsdb.backend.BackEndMultiplexer;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.backend.file.FileBackEndManager;
import org.openscada.hsdb.backend.file.FileBackEndManagerFactory;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.concurrent.HsdbThreadFactory;
import org.openscada.hsdb.configuration.Conversions;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage service that manages available storage historical item services.
 * @author Ludwig Straub
 */
public class StorageService implements SelfManagedConfigurationFactory
{
    /** Id of the OSGi service factory. */
    public final static String FACTORY_ID = "hd.storage.factory";

    /** Description of the service. */
    public final static String SERVICE_DESCRIPTION = "OpenSCADA Storage Manager Service";

    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( StorageService.class );

    /** System property defining the default root folder of the file fragments that are created by the back end objects. */
    private final static String FILE_FRAGMENTS_ROOT_FOLDER_SYSTEM_PROPERTY = "org.openscada.hd.server.storage.file.root";

    /** System property defining the maximum compression level at which connections to a file should be kept open as long as the file might be used. */
    private final static String MAX_COMPRESSION_LEVEL_TO_KEEP_FILE_OPEN = "org.openscada.hd.server.storage.file.keep.open";

    /** System property defining the flag specifying whether the service should run in import mode or not. Import mode means that no old data is removed. */
    private final static String IMPORT_MODE = "org.openscada.hd.server.storage.import";

    /** Execute heart beat period in milliseconds. */
    private final static long HEART_BEATS_PERIOD = 1000;

    /** Period in milliseconds between two consecutive attempts to delete old data. */
    private final static long CLEANER_TASK_PERIOD = 1000 * 60;

    /** Maximum data age of heart beat data. */
    private final static long PROPOSED_HEART_BEAT_DATA_AGE = 1;

    /** Internal configuration id for heartbeat back end. */
    private final static String HEARTBEAT_CONFIGURATION_ID = "HEARTBEAT";

    /** Internal id for heart beat thread. */
    private final static String HEARTBEAT_THREAD_ID = "hd.Heartbeat";

    /** Internal id for relicts cleaner thread. */
    private final static String RELICT_CLEANER_THREAD_ID = "hd.RelictCleaner";

    /** OSGi bundle context. */
    private final BundleContext bundleContext;

    /** Currently registered storage historical item services mapped by configuration id. */
    private final Map<String, StorageHistoricalItemService> services;

    /** Back end factory that has to be used. */
    private final FileBackEndFactory backEndFactory;

    /** Back end factory that has to be used. */
    private final FileBackEndManagerFactory fileBackEndManagerFactory;

    /** Registered configuration listeners. */
    private final List<ConfigurationListener> configurationListeners;

    /** Flag indicating whether old data should be deleted or not. */
    private final boolean importMode;

    /** Back end used to store heart beat data. */
    private BackEndMultiplexer heartBeatBackEnd;

    /** Task that will create periodical entries in the heart bear back end. */
    private ScheduledExecutorService heartBeatTask;

    /** Latest time with valid information that could be retrieved via the heart beat task. */
    private long latestReliableTime;

    /** Task that is used for deleting old data. */
    private ScheduledExecutorService relictCleanerTask;

    /**
     * Constructor.
     * @param bundleContext OSGi bundle context
     */
    public StorageService ( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        services = new HashMap<String, StorageHistoricalItemService> ();
        final String rootFolder = System.getProperty ( FILE_FRAGMENTS_ROOT_FOLDER_SYSTEM_PROPERTY );
        final File root = rootFolder != null ? new File ( rootFolder ) : bundleContext.getDataFile ( "" );
        final String rootPath = root.getPath ();
        if ( !root.exists () && !root.mkdirs () )
        {
            logger.error ( String.format ( "could not create root folder for data storage (%s)", rootPath ) );
        }
        backEndFactory = new FileBackEndFactory ( rootPath, Conversions.parseLong ( System.getProperty ( MAX_COMPRESSION_LEVEL_TO_KEEP_FILE_OPEN ), -1 ) );
        fileBackEndManagerFactory = new FileBackEndManagerFactory ( backEndFactory );
        configurationListeners = new LinkedList<ConfigurationListener> ();
        heartBeatBackEnd = null;
        latestReliableTime = Long.MIN_VALUE;
        importMode = Boolean.parseBoolean ( System.getProperty ( IMPORT_MODE ) );
    }

    /**
     * This method starts the heart beat functionality.
     */
    private void initializeHeartBeat ()
    {
        final long now = System.currentTimeMillis ();
        StorageChannelMetaData[] existingMetaData = null;
        try
        {
            existingMetaData = backEndFactory.getExistingBackEndsMetaData ( HEARTBEAT_CONFIGURATION_ID, true );
        }
        catch ( final Exception e )
        {
            logger.error ( "unable to retrieve existing heart beat back end meta data", e );
        }

        // create new backend if none exist
        StorageChannelMetaData metaData = null;
        final boolean createNewBackEnd = existingMetaData == null || existingMetaData.length == 0;
        if ( createNewBackEnd )
        {
            metaData = new StorageChannelMetaData ( HEARTBEAT_CONFIGURATION_ID, CalculationMethod.NATIVE, new long[0], 0, now, now + 1, PROPOSED_HEART_BEAT_DATA_AGE, 0, DataType.LONG_VALUE );
        }
        else
        {
            metaData = existingMetaData[existingMetaData.length - 1];
        }
        try
        {
            final org.openscada.hsdb.configuration.Configuration configuration = Conversions.convertMetaDatasToConfiguration ( new StorageChannelMetaData[] { metaData } );
            configuration.getData ().put ( ConfigurationImpl.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0, "" + 1 );
            final FileBackEndManager manager = fileBackEndManagerFactory.getBackEndManager ( configuration, true );
            manager.initialize ();
            final BackEndMultiplexer backEnd = new BackEndMultiplexer ( manager );
            backEnd.initialize ( metaData );
            heartBeatBackEnd = backEnd;
        }
        catch ( final Exception e )
        {
            logger.error ( String.format ( "unable to create heart beat back end (%s)", metaData ), e );
        }
    }

    /**
     * This method stores a new value in the heart beat back end to remember the last valid time.
     */
    public synchronized void performPingOfLife ()
    {
        if ( heartBeatBackEnd != null )
        {
            final long now = System.currentTimeMillis ();
            final LongValue value = new LongValue ( now, 1, 0, 0, now );
            try
            {
                heartBeatBackEnd.updateLong ( value );
                heartBeatBackEnd.cleanupRelicts ();
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "unable to store heart beat value" ), e );
            }
        }
    }

    /**
     * This method stops the heart beat functionality.
     */
    private void deinitializeHeartBeat ()
    {
        if ( heartBeatBackEnd != null )
        {
            synchronized ( this )
            {
                heartBeatTask.shutdown ();
                heartBeatTask = null;
                performPingOfLife ();
                try
                {
                    heartBeatBackEnd.deinitialize ();
                }
                catch ( final Exception e )
                {
                    logger.warn ( "could not deinitialize heart beat back end", e );
                }
                heartBeatBackEnd = null;
            }
        }
    }

    /**
     * This method creates a new service using the passed configuration as input.
     * @param backEndManager manager that will be used to handle and distribute back end objects
     * @throws Exception if the service or related back end objects could not be created
     */
    private Configuration createAndAddService ( final BackEndManager<?> backEndManager )
    {
        try
        {
            backEndManager.initialize ();
        }
        catch ( final Exception e )
        {
            final String message = String.format ( "back end manager for configuration with id '%s' could not be initialized", backEndManager.getConfiguration ().getId () );
            logger.error ( message );
            final ConfigurationImpl configuration = new ConfigurationImpl ( backEndManager.getConfiguration () );
            configuration.setFactoryId ( FACTORY_ID );
            configuration.setState ( ConfigurationState.ERROR );
            configuration.setErrorInformation ( e );
            return configuration;
        }
        final StorageHistoricalItemService service = new StorageHistoricalItemService ( backEndManager, latestReliableTime, importMode );
        final ConfigurationImpl configuration = new ConfigurationImpl ( backEndManager.getConfiguration () );
        configuration.setFactoryId ( FACTORY_ID );
        configuration.setState ( ConfigurationState.APPLIED );
        configuration.setErrorInformation ( null );
        services.put ( configuration.getId (), service );
        service.start ( bundleContext );
        final Configuration[] addedConfigurationIds = new Configuration[] { configuration };
        for ( final ConfigurationListener listener : configurationListeners )
        {
            listener.configurationUpdate ( addedConfigurationIds, null );
        }
        return configuration;
    }

    /**
     * This method loads the configuration of the service and publishes the available StorageHistoricalItemService objects.
     */
    public synchronized void start ()
    {
        // activate heart beat functionality
        initializeHeartBeat ();

        // get latest reliable time and start heart beat task
        latestReliableTime = Long.MIN_VALUE;
        if ( heartBeatBackEnd != null )
        {
            // get latest reliable time before system startup
            final long now = System.currentTimeMillis ();
            try
            {
                final LongValue[] longValues = heartBeatBackEnd.getLongValues ( now, now + 1 );
                if ( ( longValues != null ) && ( longValues.length > 0 ) )
                {
                    latestReliableTime = longValues[longValues.length - 1].getValue ();
                }
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "unable to read heart beat value" ), e );
            }

            // start heart beat task
            heartBeatTask = Executors.newSingleThreadScheduledExecutor ( HsdbThreadFactory.createFactory ( HEARTBEAT_THREAD_ID ) );
            heartBeatTask.scheduleWithFixedDelay ( new Runnable () {
                public void run ()
                {
                    performPingOfLife ();
                }
            }, HEART_BEATS_PERIOD, HEART_BEATS_PERIOD, TimeUnit.MILLISECONDS );
        }

        // get information of existing configurations
        for ( final FileBackEndManager backEndManager : fileBackEndManagerFactory.getBackEndManagers () )
        {
            if ( !HEARTBEAT_CONFIGURATION_ID.equals ( backEndManager.getConfiguration ().getId () ) )
            {
                createAndAddService ( backEndManager );
            }
        }

        // start clean relicts timer
        relictCleanerTask = Executors.newSingleThreadScheduledExecutor ( HsdbThreadFactory.createFactory ( RELICT_CLEANER_THREAD_ID ) );
        relictCleanerTask.scheduleWithFixedDelay ( new Runnable () {
            public void run ()
            {
                cleanupRelicts ();
            }
        }, CLEANER_TASK_PERIOD, CLEANER_TASK_PERIOD, TimeUnit.MILLISECONDS );
    }

    /**
     * This method loads the configuration of the service and publishes the available StorageHistoricalItemService objects.
     */
    public synchronized void stop ()
    {
        if ( relictCleanerTask != null )
        {
            relictCleanerTask.shutdown ();
            relictCleanerTask = null;
        }
        for ( final StorageHistoricalItemService service : services.values () )
        {
            service.stop ();
        }
        services.clear ();
        deinitializeHeartBeat ();
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#addConfigurationListener
     */
    public void addConfigurationListener ( final ConfigurationListener listener )
    {
        if ( !configurationListeners.contains ( listener ) )
        {
            configurationListeners.add ( listener );
            final List<Configuration> configurations = new ArrayList<Configuration> ();
            for ( final StorageHistoricalItemService service : services.values () )
            {
                final ConfigurationImpl configuration = new ConfigurationImpl ( service.getBackEndManager ().getConfiguration () );
                configuration.setState ( ConfigurationState.APPLIED );
                configurations.add ( configuration );
            }
            if ( !configurations.isEmpty () )
            {
                listener.configurationUpdate ( configurations.toArray ( new Configuration[0] ), null );
            }
        }
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#removeConfigurationListener
     */
    public void removeConfigurationListener ( final ConfigurationListener listener )
    {
        configurationListeners.remove ( listener );
    }

    /**
     * This method fills the passed map with default settings if it is passed empty.
     * @param properties map to be filled with default settings if map is passed empty
     */
    private static void fillConfigurationDefaultSettings ( final Map<String, String> properties )
    {
        if ( properties != null && properties.isEmpty () )
        {
            properties.put ( ConfigurationImpl.PROPOSED_DATA_AGE_KEY_PREFIX + 0, "2d" );
            properties.put ( ConfigurationImpl.PROPOSED_DATA_AGE_KEY_PREFIX + 1, "90d" );
            properties.put ( ConfigurationImpl.PROPOSED_DATA_AGE_KEY_PREFIX + 2, "5y" );
            properties.put ( ConfigurationImpl.COMPRESSION_TIMESPAN_KEY_PREFIX + 1, "1s" );
            properties.put ( ConfigurationImpl.COMPRESSION_TIMESPAN_KEY_PREFIX + 2, "1h" );
            properties.put ( ConfigurationImpl.ACCEPTED_TIME_DELTA_KEY, "10m" );
            properties.put ( ConfigurationImpl.MAX_COMPRESSION_LEVEL, "2" );
            properties.put ( ConfigurationImpl.DATA_TYPE_KEY, DataType.convertDataTypeToShortString ( DataType.DOUBLE_VALUE ) );
            properties.put ( ConfigurationImpl.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0, "1d" );
            properties.put ( ConfigurationImpl.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 1, "1d" );
            properties.put ( ConfigurationImpl.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 2, "1y" );
        }
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#update
     */
    public NotifyFuture<Configuration> update ( final String configurationId, final Map<String, String> inputProperties )
    {
        // avoid configurations with the same id as the heart beat configuration
        if ( HEARTBEAT_CONFIGURATION_ID.equals ( configurationId ) )
        {
            final String message = String.format ( "the configuration id '%s' is reserved for internal usage. please use a different id", configurationId );
            logger.error ( message );
            throw new RuntimeException ( message );
        }

        // provide default settings
        final Map<String, String> properties = new HashMap<String, String> ();
        if ( inputProperties != null )
        {
            properties.putAll ( inputProperties );
        }
        fillConfigurationDefaultSettings ( properties );
        if ( !properties.containsKey ( ConfigurationImpl.CALCULATION_METHODS ) )
        {
            properties.put ( ConfigurationImpl.CALCULATION_METHODS, CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.AVERAGE ) + "," + CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.MINIMUM ) + "," + CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.MAXIMUM ) );
        }

        // prepare temporary configuration from which data will be converted
        final ConfigurationImpl configuration = new ConfigurationImpl ();
        configuration.setData ( properties );
        configuration.setFactoryId ( FACTORY_ID );
        configuration.setId ( configurationId );
        configuration.setState ( ConfigurationState.ERROR );

        // disallow update of already existing service
        final StorageHistoricalItemService service = services.get ( configurationId );
        if ( service != null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "unable to modify exisiting configuration" ).fillInStackTrace () );
        }

        // try to create new service
        try
        {
            return new InstantFuture<Configuration> ( createAndAddService ( fileBackEndManagerFactory.getBackEndManager ( configuration, true ) ) );
        }
        catch ( final Exception e )
        {
            final String message = String.format ( "could not update configuration '%s'", configurationId );
            logger.error ( message, e );
            return new InstantErrorFuture<Configuration> ( new Exception ( message, e ).fillInStackTrace () );
        }
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#delete
     */
    public synchronized NotifyFuture<Configuration> delete ( final String configurationId )
    {
        // stop service
        final StorageHistoricalItemService service = services.remove ( configurationId );
        if ( service == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( String.format ( "Unable to delete non existing service with configuration id '%s'", configurationId ) ).fillInStackTrace () );
        }
        service.stop ();

        // delete relicts
        final BackEndManager<?> manager = service.getBackEndManager ();
        final ConfigurationImpl configuration = new ConfigurationImpl ( manager.getConfiguration () );
        manager.delete ();

        // send notification of changed state
        configuration.setState ( ConfigurationState.AVAILABLE );
        final String[] removedConfigurationIds = new String[] { configurationId };
        for ( final ConfigurationListener listener : configurationListeners )
        {
            listener.configurationUpdate ( null, removedConfigurationIds );
        }
        return new InstantFuture<Configuration> ( configuration );
    }

    /**
     * This method cleans old data.
     * @see org.openscada.hsdb.relict.RelictCleaner#cleanupRelicts
     */
    public synchronized void cleanupRelicts ()
    {
        logger.info ( "triggering cleaning of old data" );
        for ( final StorageHistoricalItemService service : services.values () )
        {
            try
            {
                service.cleanupRelicts ();
            }
            catch ( final Exception e )
            {
                logger.error ( "problem while cleaning relicts", e );
            }
        }
    }
}
