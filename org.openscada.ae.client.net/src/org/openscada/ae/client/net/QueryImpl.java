package org.openscada.ae.client.net;

import org.openscada.ae.Event;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.QueryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private final long queryId;

    private QueryListener listener;

    private final ConnectionImpl connection;

    public QueryImpl ( final ConnectionImpl connection, final long queryId, final QueryListener listener )
    {
        this.connection = connection;
        this.queryId = queryId;
        this.listener = listener;
    }

    public void close ()
    {
        this.connection.closeQuery ( this.queryId );
    }

    public void loadMore ( final int count )
    {
        if ( count <= 0 )
        {
            throw new IllegalArgumentException ( "'count' must be greater than zero" );
        }

        this.connection.loadMore ( this.queryId, count );
    }

    public void handleStateChange ( final QueryState state )
    {
        try
        {
            this.listener.queryStateChanged ( state );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle state change", e );
        }
    }

    public synchronized void dispose ()
    {
        if ( this.listener != null )
        {
            handleStateChange ( QueryState.DISCONNECTED );
            this.listener = null;
        }
    }

    public void handleData ( final Event[] data )
    {
        try
        {
            this.listener.queryData ( data );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle data change", e );
        }
    }
}
