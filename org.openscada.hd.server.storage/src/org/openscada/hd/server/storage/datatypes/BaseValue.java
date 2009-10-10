package org.openscada.hd.server.storage.datatypes;

/**
 * Base class for all values that can be handled via the storage channel api.
 * Two instances of this class are equal if they have the identical timestamp.
 * @author Ludwig Straub
 */
public abstract class BaseValue implements Comparable<BaseValue>
{
    /** Time stamp of the data. */
    private long time;

    /** Quality information of the data. The value lies within the interval 0..100. */
    private double qualityIndicator;

    /** Count of values that have been combined to get the current value. */
    private long baseValueCount;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     * @param baseValueCount count of values that have been combined to get the current value
     */
    public BaseValue ( final long time, final double qualityIndicator, final long baseValueCount )
    {
        this.time = time;
        this.qualityIndicator = qualityIndicator;
        this.baseValueCount = baseValueCount;
    }

    /**
     * This method returns the time stamp of the data.
     * @return time stamp of the data
     */
    public long getTime ()
    {
        return time;
    }

    /**
     * This method sets the time stamp of the data.
     * @param time time stamp of the data
     */
    public void setTime ( final long time )
    {
        this.time = time;
    }

    /**
     * This method returns the quality information of the data.
     * @return quality information of the data
     */
    public double getQualityIndicator ()
    {
        return qualityIndicator;
    }

    /**
     * This method sets the quality information of the data.
     * @param qualityIndicator quality information of the data
     */
    public void setQualityIndicator ( final double qualityIndicator )
    {
        this.qualityIndicator = qualityIndicator;
    }

    /**
     * This method returns the count of values that have been combined to get the current value.
     * @return count of values that have been combined to get the current value
     */
    public long getBaseValueCount ()
    {
        return baseValueCount;
    }

    /**
     * This method sets the count of values that have been combined to get the current value.
     * @param baseValueCount count of values that have been combined to get the current value
     */
    public void setBaseValueCount ( final long baseValueCount )
    {
        this.baseValueCount = baseValueCount;
    }

    /**
     * This method creates an object of the same type with the identical value
     * @param time time stamp of the object that has to be created
     * @param qualityIndicator quality information of the data
     * @param baseValueCount count of values that have been combined to get the current value
     * @return object of the same time with quality indicator set to 0
     */
    public abstract BaseValue createNewValue ( final long time, final double qualityIndicator, final long baseValueCount );

    /**
     * @see java.lang.Object#hashCode
     */
    public int hashCode ()
    {
        return (int) ( time ^ ( time >>> 32 ) );
    }

    /**
     * @see java.lang.Object#equals
     */
    public boolean equals ( Object baseValue )
    {
        return ( baseValue instanceof BaseValue ) && ( time == ( (BaseValue)baseValue ).getTime () );
    }

    /**
     * @see java.lang.Comparable#compareTo
     */
    public int compareTo ( BaseValue o2 )
    {
        if ( o2 == null )
        {
            return 1;
        }
        final long t2 = o2.getTime ();
        if ( time < t2 )
        {
            return -1;
        }
        if ( time > t2 )
        {
            return 1;
        }
        return 0;
    }
}
