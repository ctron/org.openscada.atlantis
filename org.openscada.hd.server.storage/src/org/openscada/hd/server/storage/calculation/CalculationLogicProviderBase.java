package org.openscada.hd.server.storage.calculation;

import org.openscada.hd.server.storage.ExtendedStorageChannel;
import org.openscada.hd.server.storage.datatypes.BaseValue;
import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.datatypes.DoubleValue;
import org.openscada.hd.server.storage.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provided methods for CalculationLogicProvider implementations.
 * @author Ludwig Straub
 */
public abstract class CalculationLogicProviderBase implements CalculationLogicProvider
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( CalculationLogicProviderBase.class );

    /** Parameter index of the virtual value calculation flag. */
    protected final static int VIRTUAL_VALUE_CALCULATION_ENABLE_INDEX = 0;

    /** Default value of the virtual value calculation flag. */
    protected final static long VIRTUAL_VALUE_CALCULATION_ENABLE_DEFAULT = 0;

    /** Parameter index of the calculation time span. */
    protected final static int TIMESPAN_FOR_CALCULATION_INDEX = 1;

    /** Default value of the calculation time span. */
    protected final static long TIMESPAN_FOR_CALCULATION_DEFAULT = 1000;

    /** Data type of the input values. */
    private final DataType inputDataType;

    /** Data type of the output values. */
    private final DataType outputDataType;

    /** Parameters further specifying the behaviour. */
    private final long[] parameters;

    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public CalculationLogicProviderBase ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
    {
        this.inputDataType = inputDataType;
        this.outputDataType = outputDataType;
        this.parameters = new long[parameters.length];
        for ( int i = 0; i < parameters.length; i++ )
        {
            this.parameters[i] = parameters[i];
        }
    }

    /**
     * This method returns the specified parameter.
     * @param index index of the parameter that has to be retrieved
     * @param defaultValue default value that will be returned if the index is not available
     * @return value of the specified parameter or the passed default value if the requested index is not available
     */
    protected long getParameterValue ( final int index, final long defaultValue )
    {
        if ( ( parameters == null ) || ( index < 0 ) || ( parameters.length <= index ) )
        {
            return defaultValue;
        }
        return parameters[index];
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#getGenerateVirtualValues
     */
    public boolean getGenerateVirtualValues ()
    {
        return getParameterValue ( VIRTUAL_VALUE_CALCULATION_ENABLE_INDEX, VIRTUAL_VALUE_CALCULATION_ENABLE_DEFAULT ) != 0;
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#getRequiredTimespanForCalculation
     */
    public long getRequiredTimespanForCalculation ()
    {
        return getParameterValue ( TIMESPAN_FOR_CALCULATION_INDEX, TIMESPAN_FOR_CALCULATION_DEFAULT );
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#getInputType
     */
    public DataType getInputType ()
    {
        return inputDataType;
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#getOutputType
     */
    public DataType getOutputType ()
    {
        return outputDataType;
    }

    /**
     * This method generates long values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values long values that were processed during the time span
     * @return calculated long values
     */
    protected LongValue[] generateLongValues ( final LongValue[] values )
    {
        return ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * This method generates long values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values double values that were processed during the time span
     * @return calculated long values
     */
    protected LongValue[] generateLongValues ( final DoubleValue[] values )
    {
        return ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * This method generates double values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values long values that were processed during the time span
     * @return calculated double values
     */
    protected DoubleValue[] generateDoubleValues ( final LongValue[] values )
    {
        return ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
    }

    /**
     * This method generates double values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values double values that were processed during the time span
     * @return calculated double values
     */
    protected DoubleValue[] generateDoubleValues ( final DoubleValue[] values )
    {
        return ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#generateValues
     */
    public BaseValue[] generateValues ( BaseValue[] values )
    {
        // check input
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            return values;
        }

        // process values
        switch ( getInputType () )
        {
        case LONG_VALUE:
        {
            LongValue[] longValues = (LongValue[])values;
            switch ( getOutputType () )
            {
            case LONG_VALUE:
            {
                return generateLongValues ( longValues );
            }
            case DOUBLE_VALUE:
            {
                return generateDoubleValues ( longValues );
            }
            default:
            {
                logger.error ( "invalid output data type specified within CalculationLogicProvider!" );
            }
            }
            break;
        }
        case DOUBLE_VALUE:
        {
            DoubleValue[] doubleValues = (DoubleValue[])values;
            switch ( getOutputType () )
            {
            case LONG_VALUE:
            {
                return generateLongValues ( doubleValues );
            }
            case DOUBLE_VALUE:
            {
                return generateDoubleValues ( doubleValues );
            }
            default:
            {
                logger.error ( "invalid output data type specified within CalculationLogicProvider!" );
            }
            }
            break;
        }
        default:
        {
            logger.error ( "invalid input data type specified within CalculationLogicProvider!" );
        }
        }
        return null;
    }
}
