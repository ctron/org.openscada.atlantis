package org.openscada.hd.server.storage.osgi;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.CalculatingStorageChannel;
import org.openscada.hd.server.storage.ExtendedStorageChannel;
import org.openscada.hd.server.storage.ExtendedStorageChannelAdapter;
import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.backend.BackEnd;
import org.openscada.hd.server.storage.backend.BackEndFactory;
import org.openscada.hd.server.storage.backend.BackEndMultiplexor;
import org.openscada.hd.server.storage.backend.FileBackEndFactory;
import org.openscada.hd.server.storage.calculation.AverageCalculationLogicProvider;
import org.openscada.hd.server.storage.calculation.CalculationLogicProvider;
import org.openscada.hd.server.storage.calculation.CalculationMethod;
import org.openscada.hd.server.storage.calculation.MaximumCalculationLogicProvider;
import org.openscada.hd.server.storage.calculation.MinimumCalculationLogicProvider;
import org.openscada.hd.server.storage.calculation.NativeCalculationLogicProvider;
import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.osgi.internal.ConfigurationImpl;
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
    public final static String FACTORY_ID = "org.openscada.hd.server.storage.osgi.StorageService";

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
     * This method creates and returns a calculation logic provider instance that supports the specified configuration.
     * @param metaData configuration that is used when creating the calculation logic provider instance
     * @return created logic provider instance
     * @throws Exception in case of unexpected problems
     */
    private CalculationLogicProvider getCalculationLogicProvider ( final StorageChannelMetaData metaData ) throws Exception
    {
        final DataType nativeDataType = metaData.getDataType ();
        final long[] calculationMethodParameters = metaData.getCalculationMethodParameters ();
        switch ( metaData.getCalculationMethod () )
        {
        case AVERAGE:
        {
            return new AverageCalculationLogicProvider ( metaData.getDetailLevelId () > 1 ? DataType.DOUBLE_VALUE : nativeDataType, DataType.DOUBLE_VALUE, calculationMethodParameters );
        }
        case MAXIMUM:
        {
            return new MaximumCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        case MINIMUM:
        {
            return new MinimumCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        case NATIVE:
        {
            return new NativeCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        default:
        {
            final String message = String.format ( "invalid calculation method specified (%s)", metaData );
            logger.error ( message );
            throw new Exception ( message );
        }
        }
    }

    /**
     * This method loads the configuration of the service and publishes the available ShiService objects.
     * @throws Exception in case of unexpected problems
     */
    public synchronized void start () throws Exception
    {
        // build a map holding all back end objects grouped by data configuration ids ordered by detail level
        for ( final StorageChannelMetaData metaData : this.backEndFactory.getExistingBackEndsMetaData () )
        {
            // create new back end object
            final BackEnd backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
            backEnd.initialize ( metaData );
            final String configurationId = metaData.getConfigurationId ();

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

        // create shi service objects for grouped configuration ids
        for ( final List<BackEnd> backEnds : this.backEndMap.values () )
        {
            if ( !backEnds.isEmpty () )
            {
                List<StorageChannelMetaData> metaDatas = new ArrayList<StorageChannelMetaData> ();
                for ( BackEnd backEnd : backEnds )
                {
                    metaDatas.add ( backEnd.getMetaData () );
                }
                createService ( Conversions.convertMetaDatasToConfiguration ( FACTORY_ID, metaDatas ), false );
            }
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

    private ConfigurationImpl createService ( final Configuration inputConfiguration, final boolean createNewBackEnds ) throws Exception
    {
        // use input data to prepare valid configuration objects
        final String configurationId = inputConfiguration.getId ();
        final List<StorageChannelMetaData> metaDatas = Conversions.convertConfigurationToMetaDatas ( FACTORY_ID, inputConfiguration );
        final ConfigurationImpl configuration = Conversions.convertMetaDatasToConfiguration ( FACTORY_ID, metaDatas );

        // create back end objects 
        final List<BackEnd> backEnds = new ArrayList<BackEnd> ();
        this.backEndMap.put ( configurationId, backEnds );
        for ( StorageChannelMetaData metaData : metaDatas )
        {
            final BackEnd backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
            backEnd.initialize ( metaData );
            if ( createNewBackEnds )
            {
                backEnd.create ( metaData );
            }
            backEnds.add ( backEnd );
        }

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
            storageChannels[i] = new CalculatingStorageChannel ( new ExtendedStorageChannelAdapter ( backEnd ), superBackEndIndex >= 0 ? storageChannels[superBackEndIndex] : null, getCalculationLogicProvider ( backEnd.getMetaData () ) );
            service.addStorageChannel ( storageChannels[i], calculationMethod );
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
