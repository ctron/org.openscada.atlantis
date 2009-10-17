package org.openscada.hd.server.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hd.server.storage.internal.Conversions;
import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannelAdapter;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.backend.BackEndFactory;
import org.openscada.hsdb.backend.BackEndMultiplexor;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage service that manages available storage historical item services.
 * @author Ludwig Straub
 */
public class StorageService implements SelfManagedConfigurationFactory, Runnable
{
    /** Id of the OSGi service factory. */
    public final static String FACTORY_ID = "hd.storage.factory";

    /** Description of the service. */
    public final static String SERVICE_DESCRIPTION = "OpenSCADA Storage Manager Service";

    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( StorageService.class );

    /** Default root folder of the file fragments that are created by the back end objects. */
    private final static String FILE_FRAGMENTS_ROOT_FOLDER_SYSTEM_PROPERTY = "org.openscada.hd.server.storage.file.root";

    /** Minimum count of file fragments before the first fragment is old enough to be deleted. */
    private final static long FILE_FRAGMENTS_PER_DATA_LIFESPAN = 4;

    /** Execute heart beat period in milliseconds. */
    private final static long HEART_BEATS_PERIOD = 1000;

    /** Maximum data age of heart beat data. */
    private final static long PROPOSED_HEART_BEAT_DATA_AGE = 1;

    /** Internal configuration id for heartbeat back end. */
    private final static String HEARTBEAT_CONFIGURATION_ID = "HEARTBEAT";

    /** OSGi bundle context. */
    private final BundleContext bundleContext;

    /** Currently registered shi services mapped by configuration id. */
    private final Map<String, ShiService> shiServices;

    /** Back end factory that has to be used. */
    private final BackEndFactory backEndFactory;

    /** Map containing all internal back end objects mapped by configuration id. */
    private final Map<String, List<BackEnd>> backEndMap;

    /** Registered configuration listeners. */
    private final List<ConfigurationListener> configurationListeners;

    /** Back end used to store heart beat data. */
    private BackEnd heartBeatBackEnd;

    /** Task that will create periodical entries in the heart bear back end. */
    private ScheduledExecutorService heartBeatTask;

    /** Latest time with valid information that could be retrieved via the heart beat task. */
    private long latestReliableTime;

    /**
     * Constructor.
     * @param bundleContext OSGi bundle context
     */
    public StorageService ( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        this.shiServices = new HashMap<String, ShiService> ();
        this.backEndMap = new HashMap<String, List<BackEnd>> ();
        final String rootFolder = System.getProperty ( FILE_FRAGMENTS_ROOT_FOLDER_SYSTEM_PROPERTY );
        final File root = rootFolder != null ? new File ( rootFolder ) : bundleContext.getDataFile ( "" );
        final String rootPath = root.getPath ();
        if ( !root.exists () && !root.mkdirs () )
        {
            logger.error ( String.format ( "could not create root folder for data storage (%s)", rootPath ) );
        }
        this.backEndFactory = new FileBackEndFactory ( rootPath );
        this.configurationListeners = new LinkedList<ConfigurationListener> ();
        heartBeatBackEnd = null;
        latestReliableTime = Long.MIN_VALUE;
    }

    /**
     * This method deinitializes all passed back end objects.
     * @param backEnds back end objects that have to be deinitialized
     */
    private static void deinitializeBackEnds ( final List<BackEnd> backEnds )
    {
        if ( backEnds != null )
        {
            for ( BackEnd backEnd : backEnds )
            {
                try
                {
                    backEnd.deinitialize ();
                }
                catch ( Exception e1 )
                {
                    StorageChannelMetaData metaData = null;
                    try
                    {
                        metaData = backEnd.getMetaData ();
                    }
                    catch ( Exception e2 )
                    {
                    }
                    logger.error ( String.format ( "unable to deinitialize unused back end for meta data '%s'", metaData ), e1 );
                }
            }
        }
    }

    /**
     * This method creates a new service using the passed configuration as input.
     * @param inputConfiguration configuration object containing the relevant information to create a service. The object does not have to be completely initialized
     * @param createNewBackEnds flag indicating whether the back end objects should be initially created or not
     * @return initialized configuration object of the created service
     * @throws Exception if the service or related back end objects could not be created
     */
    private Configuration createService ( final Configuration inputConfiguration, final boolean createNewBackEnds ) throws Exception
    {
        // use input data to prepare valid configuration objects
        final String configurationId = inputConfiguration.getId ();
        final List<StorageChannelMetaData> metaDatas = Conversions.convertConfigurationToMetaDatas ( FACTORY_ID, inputConfiguration );
        final ConfigurationImpl configuration = Conversions.convertMetaDatasToConfiguration ( FACTORY_ID, metaDatas );

        // create back end objects 
        final List<BackEnd> backEnds = new ArrayList<BackEnd> ();
        for ( StorageChannelMetaData metaData : metaDatas )
        {
            try
            {
                final BackEnd backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
                backEnds.add ( backEnd );
                backEnd.initialize ( metaData );
                if ( createNewBackEnds )
                {
                    backEnd.delete ();
                    backEnd.create ( metaData );
                }
            }
            catch ( Exception e )
            {
                deinitializeBackEnds ( backEnds );
                String message = String.format ( "could not create all back ends required for configuration '%s'", configurationId );
                logger.error ( message, e );
                throw new Exception ( message, e );
            }
        }
        this.backEndMap.put ( configurationId, backEnds );

        // create hierarchical storage channel structure
        final CalculatingStorageChannel[] storageChannels = new CalculatingStorageChannel[backEnds.size ()];
        final ShiService service = new ShiService ( configuration, latestReliableTime );
        for ( int i = 0; i < backEnds.size (); i++ )
        {
            final BackEnd backEnd = backEnds.get ( i );
            final CalculationMethod calculationMethod = backEnd.getMetaData ().getCalculationMethod ();
            int superBackEndIndex = -1;
            for ( int j = i - 1; j >= 0; j-- )
            {
                final BackEnd superBackEndCandidate = backEnds.get ( j );
                final CalculationMethod superCalculationMethod = superBackEndCandidate.getMetaData ().getCalculationMethod ();
                if ( ( superCalculationMethod == calculationMethod ) || ( superCalculationMethod == CalculationMethod.NATIVE ) )
                {
                    superBackEndIndex = j;
                    break;
                }
            }
            storageChannels[i] = new CalculatingStorageChannel ( new ExtendedStorageChannelAdapter ( backEnd ), superBackEndIndex >= 0 ? storageChannels[superBackEndIndex] : null, Conversions.getCalculationLogicProvider ( backEnd.getMetaData () ) );
            if ( superBackEndIndex >= 0 )
            {
                storageChannels[superBackEndIndex].registerStorageChannel ( storageChannels[i] );
            }
            service.addStorageChannel ( storageChannels[i] );
        }
        this.shiServices.put ( configuration.getId (), service );

        // publish service
        final Dictionary<String, String> serviceProperties = new Hashtable<String, String> ();
        serviceProperties.put ( Constants.SERVICE_PID, configurationId );
        serviceProperties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        serviceProperties.put ( Constants.SERVICE_DESCRIPTION, "A OpenSCADA Storage Historical Item Implementation" );
        service.start ();
        this.bundleContext.registerService ( new String[] { ShiService.class.getName (), StorageHistoricalItem.class.getName () }, service, serviceProperties );

        // notify listeners of performed update
        final Configuration[] addedConfigurationIds = new Configuration[] { configuration };
        for ( final ConfigurationListener listener : this.configurationListeners )
        {
            listener.configurationUpdate ( addedConfigurationIds, null );
        }
        return configuration;
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
            existingMetaData = backEndFactory.getExistingBackEndsMetaData ( HEARTBEAT_CONFIGURATION_ID );
        }
        catch ( Exception e )
        {
            logger.error ( "unable to retrieve existing heart beat back end meta data", e );
        }

        // create new backend if none exist
        StorageChannelMetaData metaData = null;
        boolean createNewBackEnd = ( existingMetaData == null ) || ( existingMetaData.length == 0 );
        if ( createNewBackEnd )
        {
            metaData = new StorageChannelMetaData ( HEARTBEAT_CONFIGURATION_ID, CalculationMethod.NATIVE, new long[0], 0, now, now + 1, PROPOSED_HEART_BEAT_DATA_AGE, DataType.LONG_VALUE );
        }
        else
        {
            metaData = existingMetaData[0];
        }
        try
        {
            final BackEnd backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
            backEnd.initialize ( metaData );
            if ( createNewBackEnd )
            {
                backEnd.delete ();
                backEnd.create ( metaData );
            }
            heartBeatBackEnd = backEnd;
        }
        catch ( Exception e )
        {
            logger.error ( String.format ( "unable to create heart beat back end (%s)", metaData ), e );
        }
    }

    /**
     * This method stores a new value in the heart beat back end to remember the last valid time.
     */
    private synchronized void performPingOfLife ()
    {
        if ( heartBeatBackEnd != null )
        {
            final long now = System.currentTimeMillis ();
            final LongValue value = new LongValue ( now, 1, 0, now );
            try
            {
                heartBeatBackEnd.updateLong ( value );
                heartBeatBackEnd.cleanupRelicts ();
            }
            catch ( Exception e )
            {
                logger.error ( String.format ( "unable to store heart beat value" ), e );
            }
        }
    }

    /**
     * This method triggers the heart beat operation.
     */
    public void run ()
    {
        this.performPingOfLife ();
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
                heartBeatBackEnd = null;
            }
        }
    }

    /**
     * This method loads the configuration of the service and publishes the available ShiService objects.
     */
    public synchronized void start ()
    {
        // activate heart beat functionality
        initializeHeartBeat ();

        // get latest reliable time and start heart beat task
        latestReliableTime = Long.MIN_VALUE;
        if ( heartBeatBackEnd != null )
        {
            final long now = System.currentTimeMillis ();
            try
            {
                LongValue[] longValues = heartBeatBackEnd.getLongValues ( now, now + 1 );
                if ( ( longValues != null ) && ( longValues.length > 0 ) )
                {
                    latestReliableTime = longValues[longValues.length - 1].getValue ();
                }
            }
            catch ( Exception e )
            {
                logger.error ( String.format ( "unable to read heart beat value" ), e );
            }
            // start heart beat task
            heartBeatTask = new ScheduledThreadPoolExecutor ( 1 );
            heartBeatTask.scheduleWithFixedDelay ( this, 0, HEART_BEATS_PERIOD, TimeUnit.MILLISECONDS );
        }

        // get information of existing meta data
        StorageChannelMetaData[] availableMetaDatas = null;
        try
        {
            availableMetaDatas = backEndFactory.getExistingBackEndsMetaData ();
        }
        catch ( Exception e )
        {
            logger.error ( "could not retrieve information of existing meta data service start", e );
            availableMetaDatas = new StorageChannelMetaData[0];
        }

        // build a map holding all back end objects grouped by data configuration ids ordered by detail level
        Set<String> bannedConfigurationIds = new HashSet<String> ();
        for ( final StorageChannelMetaData metaData : availableMetaDatas )
        {
            // ignore heartbeat meta data since it is internal
            if ( HEARTBEAT_CONFIGURATION_ID.equals ( metaData.getConfigurationId () ) )
            {
                continue;
            }

            // process meta data
            final String configurationId = metaData.getConfigurationId ();
            BackEnd backEnd = null;
            try
            {
                // check if configuration is not on the ban list
                if ( bannedConfigurationIds.contains ( configurationId ) )
                {
                    logger.info ( String.format ( "skipping meta data '%s' at service start", metaData ) );
                    continue;
                }

                // create new back end object
                backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
                backEnd.initialize ( metaData );

                // get list of already created back end objects with the same configuration id
                List<BackEnd> backEnds = this.backEndMap.get ( configurationId );
                if ( backEnds == null )
                {
                    backEnds = new LinkedList<BackEnd> ();
                    this.backEndMap.put ( configurationId, backEnds );
                }

                // assure that the list is sorted by detail level
                int insertionIndex = 0;
                while ( insertionIndex < backEnds.size () )
                {
                    if ( backEnds.get ( insertionIndex ).getMetaData ().getDetailLevelId () >= metaData.getDetailLevelId () )
                    {
                        break;
                    }
                    insertionIndex++;
                }
                backEnds.add ( insertionIndex, backEnd );
            }
            catch ( Exception e )
            {
                logger.error ( String.format ( "problem while loading back ends for meta data '%s'", metaData ), e );
                bannedConfigurationIds.add ( configurationId );
                List<BackEnd> backEnds = this.backEndMap.remove ( configurationId );
                if ( backEnds == null )
                {
                    backEnds = new ArrayList<BackEnd> ();
                }
                if ( backEnd != null )
                {
                    backEnds.add ( backEnd );
                }
                deinitializeBackEnds ( backEnds );
            }
        }

        // create shi service objects for grouped configuration ids
        bannedConfigurationIds.clear ();
        for ( Entry<String, List<BackEnd>> entry : backEndMap.entrySet () )
        {
            List<BackEnd> backEnds = entry.getValue ();
            if ( !backEnds.isEmpty () )
            {
                try
                {
                    List<StorageChannelMetaData> metaDatas = new ArrayList<StorageChannelMetaData> ();
                    for ( BackEnd backEnd : backEnds )
                    {
                        metaDatas.add ( backEnd.getMetaData () );
                    }
                    createService ( Conversions.convertMetaDatasToConfiguration ( FACTORY_ID, metaDatas ), false );
                }
                catch ( Exception e )
                {
                    final String configurationId = entry.getKey ();
                    logger.error ( String.format ( "could not create service for existing configuration '%s'", configurationId ), e );
                    bannedConfigurationIds.add ( configurationId );
                }
            }
        }

        // remove not used back end objects
        for ( String configurationId : bannedConfigurationIds )
        {
            deinitializeBackEnds ( backEndMap.remove ( configurationId ) );
        }
    }

    /**
     * This method loads the configuration of the service and publishes the available ShiService objects.
     */
    public synchronized void stop ()
    {
        for ( final ShiService shiService : shiServices.values () )
        {
            shiService.stop ();
        }
        shiServices.clear ();
        for ( final List<BackEnd> backEnds : backEndMap.values () )
        {
            deinitializeBackEnds ( backEnds );
        }
        backEndMap.clear ();
        deinitializeHeartBeat ();
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#addConfigurationListener
     */
    public void addConfigurationListener ( final ConfigurationListener listener )
    {
        if ( !this.configurationListeners.contains ( listener ) )
        {
            this.configurationListeners.add ( listener );
            final List<Configuration> configurations = new ArrayList<Configuration> ();
            for ( final ShiService service : this.shiServices.values () )
            {
                configurations.add ( service.getConfiguration () );
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
        this.configurationListeners.remove ( listener );
    }

    /**
     * This method fills the passed map with default setttings if it is passed empty.
     * @param properties map to be filled with default settings if map is passsed empty
     */
    private static void fillConfigurationDefaultSettings ( final Map<String, String> properties )
    {
        if ( ( properties != null ) && properties.isEmpty () )
        {
            properties.put ( Conversions.PROPOSED_DATA_AGE_KEY_PREFIX + 0, "1m" );
            properties.put ( Conversions.PROPOSED_DATA_AGE_KEY_PREFIX + 1, "10m" );
            properties.put ( Conversions.COMPRESSION_TIMESPAN_KEY_PREFIX + 1, "1s" );
            properties.put ( Conversions.MAX_COMPRESSION_LEVEL, "1" );
            properties.put ( Conversions.DATA_TYPE_KEY, "DV" );
        }
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#update
     */
    public NotifyFuture<Configuration> update ( final String configurationId, final Map<String, String> inputProperties )
    {
        // provide default settings
        final Map<String, String> properties = new HashMap<String, String> ();
        if ( inputProperties != null )
        {
            properties.putAll ( inputProperties );
        }
        fillConfigurationDefaultSettings ( properties );

        // prepare temporary configuration from which data will be converted
        ConfigurationImpl configuration = new ConfigurationImpl ();
        configuration.setData ( properties );
        configuration.setFactoryId ( FACTORY_ID );
        configuration.setId ( configurationId );
        configuration.setState ( ConfigurationState.ERROR );

        // disallow update of already existing service
        ShiService service = this.shiServices.get ( configurationId );
        if ( service != null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "unable to modify exisiting configuration" ).fillInStackTrace () );
        }

        // try to create new service
        try
        {
            return new InstantFuture<Configuration> ( createService ( configuration, true ) );
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
        final ShiService serviceToDelete = this.shiServices.remove ( configurationId );
        if ( serviceToDelete == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( String.format ( "Unable to delete non existing service with configuration id '%s'", configurationId ) ).fillInStackTrace () );
        }
        serviceToDelete.stop ();
        final ConfigurationImpl configuration = new ConfigurationImpl ( serviceToDelete.getConfiguration () );
        final List<BackEnd> backEnds = this.backEndMap.get ( configurationId );
        if ( backEnds != null )
        {
            for ( final BackEnd backEnd : backEnds )
            {
                try
                {
                    synchronized ( backEnd )
                    {
                        backEnd.delete ();
                        backEnd.deinitialize ();
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "could not delete back ends for configuration '%s'", configurationId ), e );
                }
            }
        }
        configuration.setState ( ConfigurationState.AVAILABLE );
        final String[] removedConfigurationIds = new String[] { configurationId };
        for ( final ConfigurationListener listener : this.configurationListeners )
        {
            listener.configurationUpdate ( null, removedConfigurationIds );
        }
        return new InstantFuture<Configuration> ( configuration );
    }
}
