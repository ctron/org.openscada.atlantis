package org.openscada.hd.server.storage.datatypes;

/**
 * Enumeration of all supported datatypes.
 * @author Ludwig Straub
 */
public enum DataType
{
    /** The datatype is unknown. */
    UNKNOWN,

    /** LongValue. */
    LONG_VALUE,

    /** DoubleValue. */
    DOUBLE_VALUE;

    /**
     * This method transforms the passed datatype value to a corresponding short string representation.
     * This method is inverse to the method convertShortStringToDataType.
     * @param dataType datatype value that has to be transformed
     * @return short string representation of the passed datatype value
     */
    public static String convertDataTypeToShortString ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return "LV";
        }
        case DOUBLE_VALUE:
        {
            return "DV";
        }
        default:
        {
            return "UNK";
        }
        }
    }

    /**
     * This method transforms the passed short string representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToShortString.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertShortStringToDataType ( final String dataType )
    {
        final String trimmedDataType = dataType == null ? "" : dataType.trim ();
        if ( "LV".equals ( trimmedDataType ) )
        {
            return LONG_VALUE;
        }
        if ( "DV".equals ( trimmedDataType ) )
        {
            return DOUBLE_VALUE;
        }
        return UNKNOWN;
    }

    /**
     * This method transforms the passed datatype value to a corresponding string representation.
     * This method is inverse to the method convertStringToDataType.
     * @param dataType datatype value that has to be transformed
     * @return string representation of the passed datatype
     */
    public static String convertDataTypeToString ( final DataType dataType )
    {
        return dataType == null ? UNKNOWN.toString () : dataType.toString ();
    }

    /**
     * This method transforms the passed string representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToString.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertStringToDataType ( final String dataType )
    {
        return dataType == null ? UNKNOWN : Enum.valueOf ( DataType.class, dataType.trim () );
    }

    /**
     * This method transforms the passed datatype value to a corresponding long representation.
     * This method is inverse to the method convertLongToDataType.
     * @param dataType datatype value that has to be transformed
     * @return long representation of the passed datatype
     */
    public static long convertDataTypeToLong ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return 0L;
        }
        case DOUBLE_VALUE:
        {
            return 1L;
        }
        default:
        {
            return -1L;
        }
        }
    }

    /**
     * This method transforms the passed long representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToLong.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertLongToDataType ( final long dataType )
    {
        switch ( (int)dataType )
        {
        case 0:
        {
            return LONG_VALUE;
        }
        case 1:
        {
            return DOUBLE_VALUE;
        }
        default:
        {
            return DataType.UNKNOWN;
        }
        }
    }
}
