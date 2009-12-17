package org.openscada.ae.server.net;

import org.openscada.ae.Event;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.QueryState;

public class QueryImpl implements Query, QueryListener
{

    private Query query;

    private final ServerConnectionHandler server;

    private final long queryId;

    public QueryImpl ( final long queryId, final ServerConnectionHandler serverConnectionHandler )
    {
        this.queryId = queryId;
        this.server = serverConnectionHandler;
    }

    public void close ()
    {
        this.query.close ();
    }

    public void loadMore ( final int count )
    {
        this.query.loadMore ( count );
    }

    public void setQuery ( final Query queryHandle )
    {
        this.query = queryHandle;
    }

    public long getQueryId ()
    {
        return this.queryId;
    }

    public void queryData ( final Event[] events )
    {
        this.server.sendQueryData ( this, events );
    }

    public void queryStateChanged ( final QueryState state )
    {
        this.server.sendQueryState ( this, state );
    }

}
