package org.openscada.ae.client.ngp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.data.EventInformation;
import org.openscada.ae.data.QueryState;
import org.openscada.core.OperationException;
import org.openscada.core.data.ErrorInformation;

public class QueryManager
{

    private final ExecutorService executor;

    private final ConnectionImpl connection;

    private final Map<Long, QueryImpl> queries = new HashMap<Long, QueryImpl> ();

    private final Random r = new Random ();

    public QueryManager ( final ExecutorService executor, final ConnectionImpl connection )
    {
        this.executor = executor;
        this.connection = connection;
    }

    public void dispose ()
    {
        onClosed ();
    }

    public void onClosed ()
    {
        for ( final QueryImpl query : this.queries.values () )
        {
            query.dispose ();
        }
        this.queries.clear ();
    }

    public void onBound ()
    {
    }

    public Query createQuery ( final String queryType, final String queryData, final QueryListener listener )
    {
        final long queryId = nextId ();
        final QueryImpl query = new QueryImpl ( this.executor, this.connection, queryId, listener );
        this.queries.put ( queryId, query );

        this.connection.sendCreateQuery ( queryId, queryType, queryData );

        return query;
    }

    private long nextId ()
    {
        long id;
        do
        {
            id = this.r.nextLong ();
        } while ( this.queries.containsKey ( id ) );
        return id;
    }

    public void updateQueryState ( final long queryId, final QueryState state, final ErrorInformation error )
    {
        final QueryImpl query = this.queries.get ( queryId );
        if ( query == null )
        {
            return;
        }

        if ( state == QueryState.DISCONNECTED )
        {
            query.dispose ();
            this.queries.remove ( queryId );
        }
        else
        {
            query.handleStateChange ( state, new OperationException ( error.getMessage () ).fillInStackTrace () );
        }
    }

    public void updateQueryData ( final long queryId, final List<EventInformation> events )
    {
        final QueryImpl query = this.queries.get ( queryId );
        if ( query == null )
        {
            return;
        }

        query.handleData ( Events.convertToEvent ( events ) );
    }

}
