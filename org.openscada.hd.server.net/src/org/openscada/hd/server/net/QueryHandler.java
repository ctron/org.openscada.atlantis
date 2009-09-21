package org.openscada.hd.server.net;

import java.util.Map;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class QueryHandler implements QueryListener
{

    private Query query;

    private final ServerConnectionHandler connectionHandler;

    private final long id;

    public QueryHandler ( final long id, final ServerConnectionHandler connectionHandler )
    {
        this.id = id;
        this.connectionHandler = connectionHandler;
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        this.connectionHandler.sendQueryData ( this.id, index, values, valueInformation );
    }

    public void updateState ( final QueryState state )
    {
        this.connectionHandler.sendQueryState ( this.id, state );
    }

    public void setQuery ( final Query query )
    {
        this.query = query;
    }

    public void close ()
    {
        this.query.close ();
    }

    public void updateParameters ( final QueryParameters parameters )
    {
        this.query.updateParameters ( parameters );
    }

}
