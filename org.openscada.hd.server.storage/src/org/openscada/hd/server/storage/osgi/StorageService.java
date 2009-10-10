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
import org.openscada.hd.server.storage.ConfigurationImpl;
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

    /** Map containing all internal back end objects mapped by data item id. */
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
     * This method creates a configuration object using the passed meta data as input.
     * @param metaDatas input for the configuration object that has to be created
     * @return created configuration object
     */
    private static ConfigurationImpl convertMetaDatasToConfiguration ( final List<StorageChannelMetaData> metaDatas )
    {
        final ConfigurationImpl configuration = new ConfigurationImpl ();
        configuration.setFactoryId ( FACTORY_ID );
        configuration.setId ( metaData.getConfigurationId () );
        final Map<String, String> data = new HashMap<String, String> ();
        configuration.setData ( data );
        configuration.setState ( ConfigurationState.APPLIED );
        data.put ( "CALCULATION_METHOD", CalculationMethod.convertCalculationMethodToString ( metaData.getCalculationMethod () ) );
        final long[] calculationMethodParameters = metaData.getCalculationMethodParameters ();
        String calculationMethodParametersAsString = "";
        if ( calculationMethodParameters != null && calculationMethodParameters.length > 0 )
        {
            final StringBuffer sb = new StringBuffer ( "" + calculationMethodParameters[0] );
            for ( int i = 1; i < calculationMethodParameters.length; i++ )
            {
                sb.append ( "" + calculationMethodParameters[i] );
            }
            calculationMethodParametersAsString = sb.toString ();
        }
        data.put ( "CALCULATION_METHOD_PARAMETERS", calculationMethodParametersAsString );
        data.put ( "START_TIME", "" + metaData.getStartTime () );
        data.put ( "END_TIME", "" + metaData.getEndTime () );
        data.put ( "PROPOSED_DATA_AGE", "" + metaData.getProposedDataAge () );
        data.put ( "DATA_TYPE", DataType.convertDataTypeToString ( metaData.getDataType () ) );
        return configuration;
    }

    /**
     * This method creates a meta data object using the passed configuration as input.
     * @param configuration input for the meta data object that has to be created
     * @return created meta data object
     */
    private static StorageChannelMetaData convertConfigurationToMetaData ( final Configuration configuration )
    {
        return createMetaData ( configuration.getId (), configuration.getData () );
    }

    /**
     * This method parsed a text to a long value and returns the result.
     * @param value text to be parsed
     * @param defaultValue default value that will be returned if either the passed text is null or if the conversion fails
     * @return converted text of default value if conversion failed
     */
    private static long parseLong ( final String value, final long defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return Long.parseLong ( value );
        }
        catch ( final Exception e )
        {
            return defaultValue;
        }
    }

    private static StorageChannelMetaData createMetaData ( final String configurationId, final Map<String, String> data )
    {
        final CalculationMethod calculationMethod = CalculationMethod.convertStringToCalculationMethod ( data.get ( "CALCULATION_METHOD" ) );
        long[] calculationMethodParameters = null;
        final String calculationMethodParametersAsString = data.get ( "CALCULATION_METHOD_PARAMETERS" );
        if ( calculationMethodParametersAsString != null )
        {
            final String[] calculationMethodParametersAsStringArray = calculationMethodParametersAsString.split ( "|" );
            calculationMethodParameters = new long[calculationMethodParametersAsStringArray.length];
            for ( int i = 0; i < calculationMethodParametersAsStringArray.length; i++ )
            {
                calculationMethodParameters[i] = parseLong ( calculationMethodParametersAsStringArray[i], 0L );
            }
        }
        final String detailLevelAsString = data.get ( "DETAIL_LEVEL" );
        final long detailLevelId = parseLong ( detailLevelAsString, 0L );
        final long startTime = parseLong ( data.get ( "START_TIME" ), Long.MIN_VALUE );
        final long endTime = parseLong ( data.get ( "END_TIME" ), Long.MAX_VALUE );
        final long proposedDataAge = parseLong ( data.get ( "PROPOSED_DATA_AGE" ), Long.MAX_VALUE );
        final DataType dataType = DataType.convertStringToDataType ( data.get ( "DATA_TYPE" ) );
        return new StorageChannelMetaData ( configurationId, calculationMethod, calculationMethodParameters, detailLevelId, startTime, endTime, proposedDataAge, dataType );
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

            // get list of already created back end objects with the same data item id
            List<BackEnd> backEnds = this.backEndMap.get ( configurationId );
            if ( backEnds == null )
            {
                backEnds = new LinkedList<BackEnd> ();
                this.backEndMap.put ( configurationId, backEnds );
            }

            // assure that the list is sorted by detail level
            final int insertionIndex = 0;
            while ( insertionIndex < backEnds.size () )
            {
                if ( backEnds.get ( insertionIndex ).getMetaData ().getDetailLevelId () >= metaData.getDetailLevelId () )
                {
                    break;
                }
            }
            backEnds.add ( insertionIndex, backEnd );
        }

        // create shi service objects for grouped data items
        for ( final List<BackEnd> backEnds : this.backEndMap.values () )
        {
            if ( !backEnds.isEmpty () )
            {
                final BackEnd backEnd = backEnds.get ( 0 );
                final StorageChannelMetaData metaData = backEnd.getMetaData ();
                if ( metaData != null )
                {
                    addService ( metaData.getConfigurationId () );
                }
            }
        }
    }

    private ShiService addService ( final String configurationId ) throws Exception
    {
        final List<BackEnd> backEnds = this.backEndMap.get ( configurationId );
        final ExtendedStorageChannel[] storageChannels = new ExtendedStorageChannel[backEnds.size ()];
        final List<StorageChannelMetaData> metaDatas = new ArrayList<StorageChannelMetaData> ();
        final ShiService shiService = new ShiService ( convertMetaDatasToConfiguration ( null ) );
        for ( int i = 0; i < backEnds.size (); i++ )
        {
            final BackEnd backEnd = backEnds.get ( i );
            final CalculationMethod calculationMethod = backEnd.getMetaData ().getCalculationMethod ();
            int superBackEndIndex = -1;
            for ( int j = i - 1; j >= 0; j-- )
            {
                final BackEnd superBackEndCandidate = backEnds.get ( i );
                final CalculationMethod superCalculationMethod = superBackEndCandidate.getMetaData ().getCalculationMethod ();
                if ( superCalculationMethod == calculationMethod || superCalculationMethod == CalculationMethod.NATIVE )
                {
                    superBackEndIndex = j;
                    break;
                }
            }
            storageChannels[i] = new CalculatingStorageChannel ( new ExtendedStorageChannelAdapter ( backEnd ), superBackEndIndex >= 0 ? storageChannels[superBackEndIndex] : null, getCalculationLogicProvider ( backEnd.getMetaData () ) );
            shiService.addStorageChannel ( storageChannels[i], calculationMethod );
        }
        this.shiServices.put ( shiService.getConfiguration ().getId (), shiService );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A OpenSCADA Storage Historical Item Implementation" );
        shiService.start ();
        this.bundleContext.registerService ( new String[] { ShiService.class.getName (), StorageHistoricalItem.class.getName () }, shiService, properties );
        return shiService;
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
        properties = new HashMap<String, String> ();
        properties.put ( "CALCULATION_METHOD", "NATIVE" );
        properties.put ( "CALCULATION_METHOD_PARAMETERS", "4711" );
        properties.put ( "DETAIL_LEVEL", "" + 0 );
        properties.put ( "START_TIME", "" + 0 );
        properties.put ( "END_TIME", "" + Long.MAX_VALUE );
        properties.put ( "PROPOSED_DATA_AGE", "" + 1000 * 60 * 10 );
        properties.put ( "DATA_TYPE", "DOUBLE_VALUE" );

        // disallow update of already existing service
        final StorageChannelMetaData metaData = createMetaData ( configurationId, properties );
        ShiService service = this.shiServices.get ( metaData.getConfigurationId () );
        if ( service == null )
        {
            // create new service
            try
            {
                final List<BackEnd> backEnds = new ArrayList<BackEnd> ();
                this.backEndMap.put ( metaData.getConfigurationId (), backEnds );
                final BackEnd backEnd = new BackEndMultiplexor ( this.backEndFactory, metaData.getProposedDataAge () / FILE_FRAGMENTS_PER_DATA_LIFESPAN );
                backEnd.initialize ( metaData );
                backEnds.add ( backEnd );
                service = addService ( metaData.getConfigurationId () );
                final Configuration[] addedConfigurationIds = new Configuration[] { service.getConfiguration () };
                for ( final ConfigurationListener listener : this.configurationListeners )
                {
                    listener.configurationUpdate ( addedConfigurationIds, null );
                }
                return new InstantFuture<Configuration> ( createEmptyConfiguration ( configurationId, ConfigurationState.APPLIED ) );
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "could not update service '%s' (%s)", configurationId, metaData ), e );
            }
        }
        return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Unable to modify exisiting configuration" ).fillInStackTrace () );
    }

    /**
     * This method creates a configuration indicating an error.
     * @param configurationId id of configuration that has to be created
     * @return configuration indicating an error
     */
    private static ConfigurationImpl createEmptyConfiguration ( final String configurationId, final ConfigurationState configurationState )
    {
        final ConfigurationImpl configuration = new ConfigurationImpl ();
        configuration.setFactoryId ( FACTORY_ID );
        configuration.setId ( configurationId );
        configuration.setState ( configurationState );
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
            return new InstantFuture<Configuration> ( createEmptyConfiguration ( configurationId, ConfigurationState.ERROR ) );
        }
        serviceToDelete.stop ();
        final ConfigurationImpl configuration = serviceToDelete.getConfiguration ();
        final StorageChannelMetaData metaData = convertConfigurationToMetaData ( configuration );
        final List<BackEnd> backEnds = this.backEndMap.get ( configurationId );
        if ( backEnds != null )
        {
            for ( final BackEnd backEnd : backEnds )
            {
                try
                {
                    backEnd.delete ();
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "could not delete back ends for '%s' (%s)", configurationId, metaData ), e );
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
