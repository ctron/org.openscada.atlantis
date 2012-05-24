package org.openscada.core.server.net;

import java.beans.ConstructorProperties;

public class StatisticInformation
{
    private final String label;

    private final Double current;

    private final Double minimum;

    private final Double maximum;

    @ConstructorProperties ( { "label", "current", "minimum", "maximum" } )
    public StatisticInformation ( final String label, final Double current, final Double minimum, final Double maximum )
    {
        super ();
        this.label = label;
        this.current = current;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public String getLabel ()
    {
        return this.label;
    }

    public Double getCurrent ()
    {
        return this.current;
    }

    public Double getMinimum ()
    {
        return this.minimum;
    }

    public Double getMaximum ()
    {
        return this.maximum;
    }

}
