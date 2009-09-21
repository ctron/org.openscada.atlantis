package org.openscada.hd.client.net;

import java.util.concurrent.Executor;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private final Executor executor;

    private final String itemId;

    private final QueryParameters parameters;

    private QueryListener listener;

    private final ConnectionImpl connection;

    private boolean closed = false;

    public QueryImpl ( final Executor executor, final ConnectionImpl connection, final String itemId, final QueryParameters parameters, final QueryListener listener )
    {
        this.executor = executor;
        this.connection = connection;
        this.itemId = itemId;
        this.parameters = parameters;
        this.listener = listener;

        synchronized ( this )
        {
            fireStateChange ( listener, QueryState.REQUESTED );
        }
    }

    public void close ()
    {

        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }
            this.closed = true;

            logger.info ( "Closing query: {} ({})", new Object[] { this.itemId, this.parameters } );
            fireStateChange ( this.listener, QueryState.DISCONNECTED );
            this.listener = null;
        }
    }

    private void fireStateChange ( final QueryListener listener, final QueryState state )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                listener.updateState ( state );
            }
        } );
    }

    public void updateParameters ( final QueryParameters parameters )
    {
        // TODO Auto-generated method stub

    }

}
