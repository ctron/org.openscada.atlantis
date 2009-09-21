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
}
