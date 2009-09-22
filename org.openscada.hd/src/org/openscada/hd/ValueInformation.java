package org.openscada.hd;

import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

@Immutable
public class ValueInformation
{
    /**
     * The percent count (from 0.0 to 1.0) of valid values
     */
    private final double quality;

    private final Calendar startTimestamp;

    private final Calendar endTimestamp;

    /**
     * The number of level 0 entries that where used to generate this value
     */
    private final long sourceValues;

    public ValueInformation ( final Calendar startTimestamp, final Calendar endTimestamp, final double quality, final long sourceValues )
    {
        super ();
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.quality = quality;
        this.sourceValues = sourceValues;
    }

    public double getQuality ()
    {
        return this.quality;
    }

    public Calendar getStartTimestamp ()
    {
        return this.startTimestamp;
    }

    public Calendar getEndTimestamp ()
    {
        return this.endTimestamp;
    }

    public long getSourceValues ()
    {
        return this.sourceValues;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%tc -> %tc (quality: %s, source values: %s)", this.startTimestamp, this.endTimestamp, this.quality, this.sourceValues );
    }

}
