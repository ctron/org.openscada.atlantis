package org.openscada.hd;

import org.openscada.utils.lang.Immutable;

@Immutable
public class Value
{
    private final Long longValue;

    private final Double doubleValue;

    public Value ( final long value )
    {
        this.longValue = value;
        this.doubleValue = null;
    }

    public Value ( final double value )
    {
        this.doubleValue = value;
        this.longValue = null;
    }

    public long toLong ()
    {
        if ( this.longValue != null )
        {
            return this.longValue;
        }
        else
        {
            return this.doubleValue.longValue ();
        }
    }

    public double toDouble ()
    {
        if ( this.doubleValue != null )
        {
            return this.doubleValue;
        }
        else
        {
            return this.longValue.doubleValue ();
        }
    }

    public Number toNumber ()
    {
        if ( this.doubleValue != null )
        {
            return this.doubleValue;
        }
        else
        {
            return this.longValue;
        }
    }
}
