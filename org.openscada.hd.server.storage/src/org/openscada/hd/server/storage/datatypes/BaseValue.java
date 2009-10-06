package org.openscada.hd.server.storage.datatypes;

/**
 * Base class for all values that can be handled via the storage channel api.
 * @author Ludwig Straub
 */
public class BaseValue
{
    /** Time stamp of the data. */
    private long time;

    /** Quality information of the data. The value lies within the interval 0..100. */
    private double qualityIndicator;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     */
    public BaseValue ( long time, double qualityIndicator )
    {
        this.time = time;
        this.qualityIndicator = qualityIndicator;
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
    public void setTime ( long time )
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
    public void setQualityIndicator ( double qualityIndicator )
    {
        this.qualityIndicator = qualityIndicator;
    }
}
