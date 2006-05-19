package org.openscada.da.client.net;

import java.net.SocketAddress;

public class ConnectionInfo
{
    private SocketAddress _remote = null;
    private boolean _autoReconnect = true;
    
    private int _reconnectDelay = Integer.getInteger ( "org.openscada.da.net.client.reconnect_delay", 10 * 1000 );

    public ConnectionInfo ()
    {
    }
    
    public ConnectionInfo ( SocketAddress remote )
    {
        super ();
        _remote = remote;
    }

    public SocketAddress getRemote ()
    {
        return _remote;
    }

    public void setRemote ( SocketAddress remote )
    {
        _remote = remote;
    }
    
    public boolean isValid ()
    {
        if ( _remote == null )
            return false;
        
        return true;
    }

    public boolean isAutoReconnect ()
    {
        return _autoReconnect;
    }

    public void setAutoReconnect ( boolean autoReconnect )
    {
        _autoReconnect = autoReconnect;
    }

    public int getReconnectDelay ()
    {
        return _reconnectDelay;
    }

    public void setReconnectDelay ( int reconnectDelay )
    {
        _reconnectDelay = reconnectDelay;
    }
    
}
