package org.openscada.hd.server.storage.calculation;

import org.openscada.hd.server.storage.datatypes.BaseValue;
import org.openscada.hd.server.storage.datatypes.DataType;

/**
 * This interface provides methods for applying calculation logic to base values and calculating new values.
 * @author Ludwig Straub
 */
public interface CalculationLogicProvider
{
    /**
     * This method returns whether all values input values should be processed without delay and passed through to other storage channels.
     * This is for instance the case for NATIVE data.
     * @return true, if data should be passed through, otherwise false
     */
    public abstract boolean getPassThroughValues ();

    /**
     * This method returns whether values should be calculated as soon as the time span is exceeded or if the calculation of values should be triggered by the processing of a new incoming value.
     * @return true, if values should be calculated as soon as the time span is exceeded, otherwise false
     */
    public abstract boolean getGenerateVirtualValues ();

    /**
     * This method returns the time span in milliseconds for which values have to be provided so that a new value or set of values can be calculated.
     * @return time span in milliseconds for which values have to be provided
     */
    public abstract long getRequiredTimespanForCalculation ();

    /**
     * This method returns the data type of the input values.
     * @return data type of the input values
     */
    public abstract DataType getInputType ();

    /**
     * This method returns the data type of the calculated values.
     * @return data type of the calculated values
     */
    public abstract DataType getOutputType ();

    /**
     * This method generates values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values values that were processed during the time span
     * @return calculated values
     */
    public abstract BaseValue[] generateValues ( final BaseValue[] values );
}
