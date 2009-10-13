package org.openscada.hd.server.storage.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;
import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.calculation.CalculationMethod;
import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.osgi.internal.ConfigurationImpl;
import org.openscada.utils.str.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides static methods for converting between different data formats.
 * @author Ludwig Straub
 */
public class Conversions
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( Conversions.class );

    /** Count of milliseconds per seconds. */
    public final static long SECOND_SPAN = 1000;

    /** Count of milliseconds per minute. */
    public final static long MINUTE_SPAN = SECOND_SPAN * 60;

    /** Count of milliseconds per hour. */
    public final static long HOUR_SPAN = MINUTE_SPAN * 60;

    /** Count of milliseconds per day. */
    public final static long DAY_SPAN = HOUR_SPAN * 24;

    /** Suffix for text values of milliseconds. */
    public final static String MILLISECOND_SPAN_SUFFIX = "ms";

    /** Suffix for text values of seconds. */
    public final static String SECOND_SPAN_SUFFIX = "s";

    /** Suffix for text values of minutes. */
    public final static String MINUTE_SPAN_SUFFIX = "m";

    /** Suffix for text values of hours. */
    public final static String HOUR_SPAN_SUFFIX = "h";

    /** Suffix for text values of days. */
    public final static String DAY_SPAN_SUFFIX = "d";

    /** Seperatur used to split the elements of a list within the configuration. */
    public final static String LIST_SEPARATOR = ",";

    /** Prefix of key in configuration for the proposed data age setting. */
    public final static String PROPOSED_DATA_AGE_KEY_PREFIX = "storage.proposedDataAge.level.";

    /** Prefix of key in configuration for the compression time span setting. */
    public final static String COMPRESSION_TIMESPAN_KEY_PREFIX = "storage.compressionTimeSpan.level.";

    /** Key in configuration for the data type setting of NATIVE calculation method setting. */
    public final static String DATA_TYPE_KEY = "storage.dataType";

    /** Key in configuration for the set of configured calculation methods setting. */
    public final static String CALCULATION_METHODS = "storage.calculationMethods";

    /** Key in configuration for the maximum compression level setting. */
    public final static String MAX_COMPRESSION_LEVEL = "storage.maxCompressionLevel";

    /**
     * This method converts the passed time span in milliseconds to a user friendly text.
     * @param timeSpan time span in milliseconds that has to be converted
     * @return user friendly text
     */
    public static String encodeTimeSpan ( final long timeSpan )
    {
        if ( ( timeSpan % DAY_SPAN ) == 0 )
        {
            return ( timeSpan / DAY_SPAN ) + DAY_SPAN_SUFFIX;
        }
        if ( ( timeSpan % HOUR_SPAN ) == 0 )
        {
            return ( timeSpan / HOUR_SPAN ) + HOUR_SPAN_SUFFIX;
        }
        if ( ( timeSpan % MINUTE_SPAN ) == 0 )
        {
            return ( timeSpan / MINUTE_SPAN ) + MINUTE_SPAN_SUFFIX;
        }
        if ( ( timeSpan % SECOND_SPAN ) == 0 )
        {
            return ( timeSpan / SECOND_SPAN ) + SECOND_SPAN_SUFFIX;
        }
        return timeSpan + MILLISECOND_SPAN_SUFFIX;
    }

    /**
     * This method converts the passed user friendly text to a valid time span in milliseconds.
     * @param timeSpan text that has to be converted
     * @return time span in milliseconds
     */
    public static long decodeTimeSpan ( final String timeSpan )
    {
        if ( timeSpan == null )
        {
            return 0;
        }
        if ( timeSpan.endsWith ( MILLISECOND_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - MILLISECOND_SPAN_SUFFIX.length () ), 0 );
        }
        if ( timeSpan.endsWith ( SECOND_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - SECOND_SPAN_SUFFIX.length () ), 0 ) * SECOND_SPAN;
        }
        if ( timeSpan.endsWith ( MINUTE_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - MINUTE_SPAN_SUFFIX.length () ), 0 ) * MINUTE_SPAN;
        }
        if ( timeSpan.endsWith ( HOUR_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - HOUR_SPAN_SUFFIX.length () ), 0 ) * HOUR_SPAN;
        }
        if ( timeSpan.endsWith ( DAY_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - DAY_SPAN_SUFFIX.length () ), 0 ) * DAY_SPAN;
        }
        return 0;
    }

    /**
     * This method creates a configuration object using the passed meta data objects as input.
     * @param factoryId id of factory that creates the configuration object
     * @param metaDatas input for the configuration object that has to be created
     * @return created configuration object
     * @throws Exception if configuration object could not be created due to missing input data
     */
    public static ConfigurationImpl convertMetaDatasToConfiguration ( final String factoryId, final List<StorageChannelMetaData> metaDatas ) throws Exception
    {
        // assure valid input
        if ( ( metaDatas == null ) || metaDatas.isEmpty () )
        {
            final String message = "no or invalid meta data objects were passed to configuration factory method";
            logger.error ( message );
            throw new Exception ( message );
        }

        // access first object to retrieve common meta data
        final ConfigurationImpl configuration = new ConfigurationImpl ();
        configuration.setFactoryId ( factoryId );
        configuration.setId ( metaDatas.get ( 0 ).getConfigurationId () );
        configuration.setState ( ConfigurationState.APPLIED );

        // fill configuration data
        final Map<String, String> data = new HashMap<String, String> ();
        configuration.setData ( data );
        long maxLevel = -1;
        final Set<String> calculationMethods = new HashSet<String> ();
        for ( StorageChannelMetaData metaData : metaDatas )
        {
            // check calculation method
            final CalculationMethod calculationMethod = metaData.getCalculationMethod ();
            if ( calculationMethod == CalculationMethod.UNKNOWN )
            {
                final String message = String.format ( "unknown calculation method specified in metadata (%s)", metaData );
                logger.error ( message );
                throw new Exception ( message );
            }
            if ( calculationMethod != CalculationMethod.NATIVE )
            {
                calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ) );
            }

            // get detail level
            final long detailLevelId = metaData.getDetailLevelId ();
            maxLevel = Math.max ( maxLevel, detailLevelId );

            // check detail level id
            if ( detailLevelId < 0 )
            {
                final String message = String.format ( "invalid detail level id specified in metadata (%s)", metaData );
                logger.error ( message );
                throw new Exception ( message );
            }

            // set proposed data age per level
            final String proposedDataAgeKey = PROPOSED_DATA_AGE_KEY_PREFIX + detailLevelId;
            if ( !data.containsKey ( proposedDataAgeKey ) )
            {
                data.put ( proposedDataAgeKey, Conversions.encodeTimeSpan ( metaData.getProposedDataAge () ) );
            }

            // set proposed compression time span per level if calculation method is not NATIVE
            if ( calculationMethod != CalculationMethod.NATIVE )
            {
                final String compressionTimeSpanKey = COMPRESSION_TIMESPAN_KEY_PREFIX + detailLevelId;
                if ( !data.containsKey ( compressionTimeSpanKey ) )
                {
                    // set compression time span per detail level
                    final long[] calculationMethodParameters = metaData.getCalculationMethodParameters ();
                    if ( calculationMethodParameters.length < 2 )
                    {
                        final String message = String.format ( "invalid calculation method parameters set (%s)!", metaData );
                        logger.error ( message );
                        throw new Exception ( message );
                    }
                    data.put ( compressionTimeSpanKey, Conversions.encodeTimeSpan ( calculationMethodParameters[1] ) );
                }
            }
            else
            {
                data.put ( DATA_TYPE_KEY, DataType.convertDataTypeToShortString ( metaData.getDataType () ) );
            }
        }

        // assure that all calculated data has been set
        if ( DataType.convertShortStringToDataType ( data.get ( DATA_TYPE_KEY ) ) == DataType.UNKNOWN )
        {
            final String message = String.format ( "no data type specified for " + CalculationMethod.convertCalculationMethodToString ( CalculationMethod.NATIVE ) + " calculation method for configuration '%s'", configuration.getId () );
            logger.error ( message );
            throw new Exception ( message );
        }

        // set common data
        data.put ( CALCULATION_METHODS, StringHelper.join ( calculationMethods, LIST_SEPARATOR ) );
        data.put ( MAX_COMPRESSION_LEVEL, "" + maxLevel );

        // the configuration is now complete
        return configuration;
    }

    /**
     * This method creates meta data objects using the passed configuration as input.
     * @param factoryId id of factory that created the configuration object
     * @param configuration input for the meta data objects that have to be created
     * @return created meta data objects
     * @throws Exception if meta data objects could not be created due to missing input data
     */
    public static List<StorageChannelMetaData> convertConfigurationToMetaDatas ( final String factoryId, final Configuration configuration ) throws Exception
    {
        // assure that factory id matches the expected value
        final String configurationId = configuration.getId ();
        final String configurationFactoryId = configuration.getFactoryId ();
        if ( ( factoryId == null ) || !factoryId.equals ( configurationFactoryId ) )
        {
            final String message = String.format ( "factory '%s' do not match factory '%s' of configuration '%s'", factoryId, configuration.getFactoryId (), configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // assure that convertable data is availavle
        final Map<String, String> data = configuration.getData ();
        if ( data == null )
        {
            final String message = String.format ( "no data available in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // check detail levels
        final long maxDetailLevelId = Conversions.parseLong ( data.get ( MAX_COMPRESSION_LEVEL ), 0 );
        if ( maxDetailLevelId < 0 )
        {
            final String message = String.format ( "invalid maximum compression level specified in configuration '%s'! value must be '0' or greater", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // check calculation methods
        final String calculationMethodsValue = data.get ( CALCULATION_METHODS );
        final Set<CalculationMethod> calculationMethods = new HashSet<CalculationMethod> ();
        if ( calculationMethodsValue != null )
        {
            for ( String s : calculationMethodsValue.split ( LIST_SEPARATOR ) )
            {
                calculationMethods.add ( CalculationMethod.convertShortStringToCalculationMethod ( s.trim () ) );
            }
        }
        calculationMethods.remove ( CalculationMethod.NATIVE );
        if ( calculationMethods.contains ( CalculationMethod.UNKNOWN ) )
        {
            final String message = String.format ( "unknown calculation method specified in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // assure the calculation methods are specified if a compression level greater than 0 is set
        if ( ( maxDetailLevelId > 0 ) && calculationMethods.isEmpty () )
        {
            final String message = String.format ( "no calculation method specified, although maximum compression level is greater 0 in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // check native data type
        final DataType nativeDataType = DataType.convertShortStringToDataType ( data.get ( DATA_TYPE_KEY ) );
        if ( nativeDataType == DataType.UNKNOWN )
        {
            final String message = String.format ( "unknown data type specified in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create meta data for native value calculation
        final List<StorageChannelMetaData> metaDatas = new ArrayList<StorageChannelMetaData> ();
        final long now = System.currentTimeMillis ();
        long proposedDataAge = decodeTimeSpan ( data.get ( PROPOSED_DATA_AGE_KEY_PREFIX + 0 ) );
        if ( proposedDataAge < 1 )
        {
            final String message = String.format ( "invalid proposed data age for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.NATIVE ), configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }
        metaDatas.add ( new StorageChannelMetaData ( configurationId, CalculationMethod.NATIVE, new long[] { 0 }, 0, now, now, proposedDataAge, nativeDataType ) );

        // create meta data for other calculation methods if required
        for ( long detailLevelId = 1; detailLevelId <= maxDetailLevelId; detailLevelId++ )
        {
            for ( CalculationMethod calculationMethod : calculationMethods )
            {
                final long compressionTimeSpan = decodeTimeSpan ( data.get ( COMPRESSION_TIMESPAN_KEY_PREFIX + detailLevelId ) );
                if ( compressionTimeSpan < 1 )
                {
                    final String message = String.format ( "invalid compression timespan specified in configuration '%s'", configurationId );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                proposedDataAge = decodeTimeSpan ( data.get ( PROPOSED_DATA_AGE_KEY_PREFIX + detailLevelId ) );
                if ( proposedDataAge < 1 )
                {
                    final String message = String.format ( "invalid proposed data age for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), configurationId );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                final long[] calculationMethodParameters = new long[2];
                calculationMethodParameters[0] = detailLevelId == 1 ? 1 : 0;
                calculationMethodParameters[1] = compressionTimeSpan;
                metaDatas.add ( new StorageChannelMetaData ( configurationId, calculationMethod, calculationMethodParameters, detailLevelId, now, now, proposedDataAge, nativeDataType ) );
            }
        }
        return metaDatas;
    }

    /**
     * This method parsed a text to a long value and returns the result.
     * @param value text to be parsed
     * @param defaultValue default value that will be returned if either the passed text is null or if the conversion fails
     * @return converted text of default value if conversion failed
     */
    public static long parseLong ( final String value, final long defaultValue )
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
}
