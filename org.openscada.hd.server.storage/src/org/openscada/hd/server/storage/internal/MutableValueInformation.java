package org.openscada.hd.server.storage.internal;

import java.util.Calendar;

/**
 * A temporary mutable version of class ValueInformation.
 * @see org.openscada.hd.ValueInformation
 * @author Ludwig Straub
 */
public class MutableValueInformation
{

    /**
     * @see startTimestamp of org.openscada.hd.ValueInformation
     */
    private Calendar startTimestamp;

    /**
     * @see endTimestamp of org.openscada.hd.ValueInformation
     */
    private Calendar endTimestamp;

    /**
     * @see quality of org.openscada.hd.ValueInformation
     */
    private double quality;

    /**
     * @see quality of org.openscada.hd.ValueInformation
     */
    private double manual;

    /**
     * @see sourceValues of org.openscada.hd.ValueInformation
     */
    private long sourceValues;

    /**
     * Standard constructor.
     * All values are set to null or 0.
     * @param startTimestamp the initial start time
     * @param endTimestamp the initial end time
     * @param quality the initial quality value
     * @param manual initial manual value
     * @param sourceValues the initial souce values value
     */
    public MutableValueInformation ( final Calendar startTimestamp, final Calendar endTimestamp, final double quality, final double manual, final long sourceValues )
    {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.quality = quality;
        this.manual = manual;
        this.sourceValues = sourceValues;
    }

    /**
     * @see org.openscada.hd.ValueInformation#getStartTimestamp
     * @return @see org.openscada.hd.ValueInformation#getStartTimestamp
     */
    public Calendar getStartTimestamp ()
    {
        return (Calendar)startTimestamp.clone ();
    }

    /**
     * @see org.openscada.hd.ValueInformation#getEndTimestamp
     * @return @see org.openscada.hd.ValueInformation#getEndTimestamp
     */
    public Calendar getEndTimestamp ()
    {
        return (Calendar)endTimestamp.clone ();
    }

    /**
     * @see org.openscada.hd.ValueInformation#getEndTimestamp
     * @return @see org.openscada.hd.ValueInformation#getEndTimestamp
     */
    public double getQuality ()
    {
        return quality;
    }

    /**
     * @see org.openscada.hd.ValueInformation#getManualPercentage
     * @return @see org.openscada.hd.ValueInformation#getManualPercentage
     */
    public double getManual ()
    {
        return manual;
    }

    /**
     * @see org.openscada.hd.ValueInformation#getSourceValues
     * @return @see org.openscada.hd.ValueInformation#getSourceValues
     */
    public long getSourceValues ()
    {
        return sourceValues;
    }

    /**
     * This method sets the start time. @see org.openscada.hd.ValueInformation
     * @param startTimestamp value to set
     */
    public void setStartTimestamp ( final Calendar startTimestamp )
    {
        this.startTimestamp = startTimestamp;
    }

    /**
     * This method sets the end time. @see org.openscada.hd.ValueInformation
     * @param endTimestamp value to set
     */
    public void setEndTimestamp ( final Calendar endTimestamp )
    {
        this.endTimestamp = endTimestamp;
    }

    /**
     * This method sets the quality value. @see org.openscada.hd.ValueInformation
     * @param quality value to set
     */
    public void setQuality ( final double quality )
    {
        this.quality = quality;
    }

    /**
     * This method sets the manual value. @see org.openscada.hd.ValueInformation
     * @param manual value to set
     */
    public void setManual ( final double manual )
    {
        this.manual = manual;
    }

    /**
     * This method sets the source values value. @see org.openscada.hd.ValueInformation
     * @param sourceValues value to set
     */
    public void setSourceValues ( final long sourceValues )
    {
        this.sourceValues = sourceValues;
    }
}
