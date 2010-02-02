package org.openscada.da.master.analyzer;

import org.openscada.core.client.ConnectionState;

public class ConnectionAnalyzerStatus
{
    private ConnectionState state;

    public ConnectionState getState ()
    {
        return this.state;
    }

    public void setState ( final ConnectionState state )
    {
        this.state = state;
    }

}
