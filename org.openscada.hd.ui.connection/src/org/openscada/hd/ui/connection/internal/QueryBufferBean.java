package org.openscada.hd.ui.connection.internal;

import java.util.Calendar;

import org.openscada.hd.QueryParameters;
import org.openscada.hd.ui.data.QueryBuffer;

public class QueryBufferBean extends QueryBuffer
{
    private final QueryWrapper parent;

    public QueryBufferBean ( final QueryWrapper queryManager, final String itemId )
    {
        super ( queryManager.getService ().getConnection (), itemId, createRequestParameters () );
        this.parent = queryManager;
    }

    private static QueryParameters createRequestParameters ()
    {
        final Calendar start = Calendar.getInstance ();
        final Calendar end = (Calendar)start.clone ();
        start.add ( Calendar.MINUTE, -500 );
        end.add ( Calendar.MINUTE, 10 );

        return new QueryParameters ( start, end, 25 );
    }

    public QueryWrapper getParent ()
    {
        return this.parent;
    }

    public void remove ()
    {
        close ();
        this.parent.removeQuery ( this );
    }
}
