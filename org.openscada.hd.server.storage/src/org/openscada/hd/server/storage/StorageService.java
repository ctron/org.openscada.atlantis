package org.openscada.hd.server.storage;

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

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.internal.ConfigurationImpl;
import org.openscada.hd.server.storage.internal.Conversions;
import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannelAdapter;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.backend.BackEndFactory;
import org.openscada.hsdb.backend.BackEndMultiplexor;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.calculation.CalculationMethod;
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
public class StorageService implements SelfManagedConfigurationFactory
{
    /** Id of the OSGi service factory. */
    public final static String FACTORY_ID = "hd.StorageService";

    /** Description of the service. */
    public final static String SERVICE_DESCRIPTION = "OpenSCADA Storage Manager Service";

    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( StorageService.class );

    /** Default root folder of the file fragments that are created by the back end objects. */
    private final static String FILE_FRAGMENTS_ROOT_FOLDER_SYSTEM_PROPERTY = "STORAGE_SERVICE_ROOT";

    /** Minimum count of file fragments before the first fragment is old enough to be deleted. */
    private final static long FILE_FRAGMENTS_PER_DATA_LIFESPAN = 4;

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
        this.backEndFactory = new FileBackEndFactory ( rootFolder != null ? rootFolder : bundleContext.getDataFile ( "" ).getPath () );
        this.configurationListeners = new LinkedList<ConfigurationListener> ();
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
        final ExtendedStorageChannel[] storageChannels = new ExtendedStorageChannel[backEnds.size ()];
        final ShiService service = new ShiService ( configuration );
        for ( int i = 0; i < backEnds.size (); i++ )
        {
            final BackEnd backEnd = backEnds.get ( i );
            final CalculationMethod calculationMethod = backEnd.getMetaData ().getCalculationMethod ();
            int superBackEndIndex = -1;
            for ( int j = i - 1; j >= 0; j-- )
            {
                final BackEnd superBackEndCandidate = backEnds.get ( i );
                final CalculationMethod superCalculationMethod = superBackEndCandidate.getMetaData ().getCalculationMethod ();
                if ( ( superCalculationMethod == calculationMethod ) || ( superCalculationMethod == CalculationMethod.NATIVE ) )
                {
                    superBackEndIndex = j;
                    break;
                }
            }
            storageChannels[i] = new CalculatingStorageChannel ( new ExtendedStorageChannelAdapter ( backEnd ), superBackEndIndex >= 0 ? storageChannels[superBackEndIndex] : null, Conversions.getCalculationLogicProvider ( backEnd.getMetaData () ) );
            service.addStorageChannel ( calculationMethod, storageChannels[i] );
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
     * This method loads the configuration of the service and publishes the available ShiService objects.
     */
    public synchronized void start ()
    {
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
     * @throws Exception in case unexpected problems
     */
    public synchronized void stop () throws Exception
    {
        for ( final ShiService shiService : this.shiServices.values () )
        {
            shiService.stop ();
        }
        this.shiServices.clear ();
        if ( this.backEndMap != null )
        {
            for ( final List<BackEnd> backEnds : this.backEndMap.values () )
            {
                for ( final BackEnd backEnd : backEnds )
                {
                    backEnd.deinitialize ();
                }
            }
            this.backEndMap.clear ();
        }
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
     * @see org.openscada.ca.SelfManagedConfigurationFactory#update
     */
    public NotifyFuture<Configuration> update ( final String configurationId, Map<String, String> properties )
    {
        // easy input test data
        if ( properties.isEmpty () )
        {
            properties = new HashMap<String, String> ();
            properties.put ( Conversions.PROPOSED_DATA_AGE_KEY_PREFIX + 0, "1m" );
            properties.put ( Conversions.PROPOSED_DATA_AGE_KEY_PREFIX + 1, "1h" );
            properties.put ( Conversions.COMPRESSION_TIMESPAN_KEY_PREFIX + 1, "1s" );
            properties.put ( Conversions.CALCULATION_METHODS, "AVG,MIN,MAX" );
            properties.put ( Conversions.MAX_COMPRESSION_LEVEL, "1" );
            properties.put ( Conversions.DATA_TYPE_KEY, "DV" );
        }

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
