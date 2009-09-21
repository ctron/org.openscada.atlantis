package org.openscada.hd.client.net;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;

public class ErrorQueryImpl implements Query
{

    public ErrorQueryImpl ( final QueryListener listener )
    {
        listener.updateState ( QueryState.DISCONNECTED );
    }

    public void close ()
    {
        // nothing to do
    }

    public void updateParameters ( final QueryParameters parameters )
    {
    }

}
