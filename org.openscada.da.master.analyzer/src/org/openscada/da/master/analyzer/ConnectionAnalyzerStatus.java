package org.openscada.da.master.analyzer;

import org.openscada.core.client.ConnectionState;

public class ConnectionAnalyzerStatus
{
    private ConnectionState state;

    private boolean connected;

    public ConnectionState getState ()
    {
        return this.state;
    }

    public void setState ( final ConnectionState state )
    {
        this.state = state;
    }

    public void setConnected ( final boolean connected )
    {
        this.connected = connected;
    }

    public boolean isConnected ()
    {
        return this.connected;
    }

}
