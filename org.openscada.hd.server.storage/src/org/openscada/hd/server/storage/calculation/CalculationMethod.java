package org.openscada.hd.server.storage.calculation;

/**
 * Enumeration of all supported calculation methods.
 * @author Ludwig Straub
 */
public enum CalculationMethod
{
    /** The calculation method is unknown. */
    UNKNOWN,

    /** No calculation is performed. All values that are processed by a storage channel that is marked as NATIVE will remain unchanged. */
    NATIVE,

    /** An average calculation is performed on the values that are processed. */
    AVERAGE,

    /** A minimum value calculation is performed on the values that are processed. */
    MINIMUM,

    /** A maximum value calculation is performed on the values that are processed. */
    MAXIMUM;

    /**
     * This method transforms the passed calculation method value to a corresponding short string representation.
     * This method is inverse to the method convertShortStringToCalculationMethod.
     * @param calculationMethod calculation method value that has to be transformed
     * @return short string representation of the passed calculation method
     */
    public static String convertCalculationMethodToShortString ( CalculationMethod calculationMethod )
    {
        switch ( calculationMethod )
        {
        case NATIVE:
        {
            return "NAT";
        }
        case AVERAGE:
        {
            return "AVG";
        }
        case MINIMUM:
        {
            return "MIN";
        }
        case MAXIMUM:
        {
            return "MAX";
        }
        default:
        {
            return "UNK";
        }
        }
    }

    /**
     * This method transforms the passed short string representation of a calculation method value to the corresponding calculation method value.
     * This method is inverse to the method convertCalculationMethodToShortString.
     * @param calculationMethod calculation method value that has to be transformed
     * @return transformed calculation method value
     */
    public static CalculationMethod convertShortStringToCalculationMethod ( String calculationMethod )
    {
        if ( "NAT".equals ( calculationMethod ) )
        {
            return CalculationMethod.NATIVE;
        }
        if ( "AVG".equals ( calculationMethod ) )
        {
            return CalculationMethod.AVERAGE;
        }
        if ( "MIN".equals ( calculationMethod ) )
        {
            return CalculationMethod.MINIMUM;
        }
        if ( "MAX".equals ( calculationMethod ) )
        {
            return CalculationMethod.MAXIMUM;
        }
        return CalculationMethod.UNKNOWN;
    }

    /**
     * This method transforms the passed calculation method value to a corresponding string representation.
     * This method is inverse to the method convertStringToCalculationMethod.
     * @param calculationMethod calculation method value that has to be transformed
     * @return string representation of the passed calculation method
     */
    public static String convertCalculationMethodToString ( CalculationMethod calculationMethod )
    {
        return calculationMethod.toString ();
    }

    /**
     * This method transforms the passed string representation of a calculation method value to the corresponding calculation method value.
     * This method is inverse to the method convertCalculationMethodToString.
     * @param calculationMethod calculation method value that has to be transformed
     * @return transformed calculation method value
     */
    public static CalculationMethod convertStringToCalculationMethod ( String calculationMethod )
    {
        return Enum.valueOf ( CalculationMethod.class, calculationMethod );
    }

    /**
     * This method transforms the passed calculation method value to a corresponding long representation.
     * This method is inverse to the method convertLongToCalculationMethod.
     * @param calculationMethod calculation method value that has to be transformed
     * @return long representation of the passed calculation method
     */
    public static long convertCalculationMethodToLong ( CalculationMethod calculationMethod )
    {
        switch ( calculationMethod )
        {
        case NATIVE:
        {
            return 0L;
        }
        case AVERAGE:
        {
            return 1L;
        }
        case MINIMUM:
        {
            return 2L;
        }
        case MAXIMUM:
        {
            return 3L;
        }
        default:
        {
            return -1L;
        }
        }
    }

    /**
     * This method transforms the passed long representation of a calculation method value to the corresponding calculation method value.
     * This method is inverse to the method convertCalculationMethodToLong.
     * @param calculationMethod calculation method value that has to be transformed
     * @return transformed calculation method value
     */
    public static CalculationMethod convertLongToCalculationMethod ( final long calculationMethod )
    {
        switch ( (int)calculationMethod )
        {
        case 0:
        {
            return CalculationMethod.NATIVE;
        }
        case 1:
        {
            return CalculationMethod.AVERAGE;
        }
        case 2:
        {
            return CalculationMethod.MINIMUM;
        }
        case 3:
        {
            return CalculationMethod.MAXIMUM;
        }
        default:
        {
            return CalculationMethod.UNKNOWN;
        }
        }
    }
}
