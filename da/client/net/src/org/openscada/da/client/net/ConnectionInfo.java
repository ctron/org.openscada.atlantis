package org.openscada.da.client.net;

import java.net.SocketAddress;

public class ConnectionInfo
{
    private SocketAddress _remote = null;

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
    
}
