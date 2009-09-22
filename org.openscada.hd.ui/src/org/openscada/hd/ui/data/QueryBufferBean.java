package org.openscada.hd.ui.data;

import java.util.Calendar;

import org.openscada.hd.QueryParameters;

public class QueryBufferBean extends QueryBuffer
{

    private final ConnectionEntryBean parent;

    private final HistoricalItemEntryBean item;

    public QueryBufferBean ( final ConnectionEntryBean parent, final HistoricalItemEntryBean item )
    {
        super ( parent.getConnection (), item.getId (), createRequestParameters () );
        this.parent = parent;
        this.item = item;
    }

    private static QueryParameters createRequestParameters ()
    {
        final Calendar start = Calendar.getInstance ();
        final Calendar end = (Calendar)start.clone ();
        start.add ( Calendar.MINUTE, -30 );
        end.add ( Calendar.MINUTE, 30 );

        return new QueryParameters ( start, end, 25 );
    }

    public HistoricalItemEntryBean getItem ()
    {
        return this.item;
    }

    public void remove ()
    {
        close ();
        this.parent.removeQuery ( this );
    }
}
