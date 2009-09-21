package org.openscada.hd;

import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

@Immutable
public class QueryParameters
{
    private final Calendar startTimestamp;

    private final Calendar endTimestamp;

    private final int numberOfEntries;

    public QueryParameters ( final Calendar startTimestamp, final Calendar endTimestamp, final int numberOfEntries )
    {
        if ( startTimestamp == null )
        {
            throw new NullPointerException ( "'startTimestamp' must not be null" );
        }

        if ( endTimestamp == null )
        {
            throw new NullPointerException ( "'endTimestamp' must not be null" );
        }

        if ( numberOfEntries <= 0 )
        {
            throw new IllegalArgumentException ( "'numberOfEntries' must be greater than zero" );
        }

        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.numberOfEntries = numberOfEntries;
    }

    public Calendar getStartTimestamp ()
    {
        return this.startTimestamp;
    }

    public Calendar getEndTimestamp ()
    {
        return this.endTimestamp;
    }

    public int getEntries ()
    {
        return this.numberOfEntries;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%1$tF-%1$tT.%1$tL -> %2$tF-%2$tT.%2$tL (%3$s)", this.startTimestamp, this.endTimestamp, this.numberOfEntries );
    }
}
