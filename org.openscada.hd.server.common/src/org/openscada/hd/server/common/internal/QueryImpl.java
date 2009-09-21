package org.openscada.hd.server.common.internal;

import java.util.Map;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class QueryImpl implements Query, QueryListener
{
    private Query query;

    private final SessionImpl session;

    private final QueryListener listener;

    public QueryImpl ( final SessionImpl session, final QueryListener listener )
    {
        this.session = session;
        this.listener = listener;
        session.addQuery ( this );
    }

    public void setQuery ( final Query query )
    {
        this.query = query;
    }

    public void close ()
    {
        this.session.removeQuery ( this );
        this.query.close ();
    }

    public void updateParameters ( final QueryParameters parameters )
    {
        this.query.updateParameters ( parameters );
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        this.listener.updateData ( index, values, valueInformation );
    }

    public void updateState ( final QueryState state )
    {
        try
        {
            this.listener.updateState ( state );
        }
        finally
        {
            switch ( state )
            {
            case DISCONNECTED:
                this.session.removeQuery ( this );
                break;
            }
        }
    }

    public void dispose ()
    {
        this.query.close ();
    }
}
