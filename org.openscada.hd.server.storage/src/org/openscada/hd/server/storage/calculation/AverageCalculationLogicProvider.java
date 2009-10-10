package org.openscada.hd.server.storage.calculation;

import org.openscada.hd.server.storage.datatypes.DataType;
import org.openscada.hd.server.storage.datatypes.DoubleValue;
import org.openscada.hd.server.storage.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the calculation of average values.
 * @author Ludwig Straub
 */
public class AverageCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public AverageCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
    {
        super ( inputDataType, outputDataType, parameters );
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProvider#getPassThroughValues
     */
    public boolean getPassThroughValues ()
    {
        return false;
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProviderBase#generateLongValues
     */
    protected LongValue[] generateLongValues ( final LongValue[] values )
    {
        final long timeSpanSize = getRequiredTimespanForCalculation ();
        double avgValue = 0;
        double quality = 0;
        long baseValueCount = 0;
        long lastTimeStamp = Long.MAX_VALUE;
        long lastValue = 0;
        for ( LongValue value : values )
        {
            long time = value.getTime ();
            if ( lastTimeStamp < time )
            {
                long weightFactor = time - lastTimeStamp;
                avgValue += lastValue * weightFactor;
                quality += value.getQualityIndicator () * weightFactor;
                baseValueCount += value.getBaseValueCount ();
                lastTimeStamp = time;
                lastValue = value.getValue ();
            }
        }
        return new LongValue[] { new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, (long) ( avgValue / timeSpanSize ) ) };
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProviderBase#generateDoubleValues
     */
    protected LongValue[] generateLongValues ( final DoubleValue[] values )
    {
        final long timeSpanSize = getRequiredTimespanForCalculation ();
        double avgValue = 0;
        double quality = 0;
        long baseValueCount = 0;
        long lastTimeStamp = Long.MAX_VALUE;
        double lastValue = 0;
        for ( DoubleValue value : values )
        {
            long time = value.getTime ();
            if ( lastTimeStamp < time )
            {
                long weightFactor = time - lastTimeStamp;
                avgValue += lastValue * weightFactor;
                quality += value.getQualityIndicator () * weightFactor;
                baseValueCount += value.getBaseValueCount ();
                lastTimeStamp = time;
                lastValue = value.getValue ();
            }
        }
        return new LongValue[] { new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, (long) ( avgValue / timeSpanSize ) ) };
    }

    /**
     * @see org.openscada.hd.server.storage.calculation.CalculationLogicProviderBase#generateDoubleValues
     */
    protected DoubleValue[] generateDoubleValues ( final LongValue[] values )
    {
        final long timeSpanSize = getRequiredTimespanForCalculation ();
        double avgValue = 0;
        double quality = 0;
        long baseValueCount = 0;
        long lastTimeStamp = Long.MAX_VALUE;
        long lastValue = 0;
        for ( LongValue value : values )
        {
            long time = value.getTime ();
            if ( lastTimeStamp < time )
            {
                long weightFactor = time - lastTimeStamp;
                avgValue += lastValue * weightFactor;
                quality += value.getQualityIndicator () * weightFactor;
                baseValueCount += value.getBaseValueCount ();
                lastTimeStamp = time;
                lastValue = value.getValue ();
            }
        }
        return new DoubleValue[] { new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, avgValue / timeSpanSize ) };
    }

    /**
     * This method generates double values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values double values that were processed during the time span
     * @return calculated double values
     */
    protected DoubleValue[] generateDoubleValues ( final DoubleValue[] values )
    {
        final long timeSpanSize = getRequiredTimespanForCalculation ();
        double avgValue = 0;
        double quality = 0;
        long baseValueCount = 0;
        long lastTimeStamp = Long.MAX_VALUE;
        double lastValue = 0;
        for ( DoubleValue value : values )
        {
            long time = value.getTime ();
            if ( lastTimeStamp < time )
            {
                long weightFactor = time - lastTimeStamp;
                avgValue += lastValue * weightFactor;
                quality += value.getQualityIndicator () * weightFactor;
                baseValueCount += value.getBaseValueCount ();
                lastTimeStamp = time;
                lastValue = value.getValue ();
            }
        }
        return new DoubleValue[] { new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, avgValue / timeSpanSize ) };
    }
}
