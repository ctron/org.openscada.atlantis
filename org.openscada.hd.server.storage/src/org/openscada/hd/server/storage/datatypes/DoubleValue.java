package org.openscada.hd.server.storage.datatypes;

/**
 * This class handles a double value for being storaged in a storage channel.
 * @author Ludwig Straub
 */
public class DoubleValue extends BaseValue
{
    /** Value to be handled. */
    private double value;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     * @param baseValueCount count of values that have been combined to get the current value
     * @param value value to be handled
     */
    public DoubleValue ( long time, double qualityIndicator, final long baseValueCount, double value )
    {
        super ( time, qualityIndicator, baseValueCount );
        this.value = value;
    }

    /**
     * This method returns the value to be handled.
     * @return value to be handled
     */
    public double getValue ()
    {
        return value;
    }

    /**
     * This method sets the value to be handled.
     * @param value value to be handled
     */
    public void setValue ( final double value )
    {
        this.value = value;
    }

    /**
     * @see org.openscada.hd.server.storage.datatypes
     */
    public BaseValue createNewValue ( final long time, final double qualityIndicator, final long baseValueCount )
    {
        return new DoubleValue ( time, qualityIndicator, baseValueCount, value );
    }
}
