package org.openscada.da.client.net;


public class ConnectionInfo
{
    private String _hostName = "";
    private int _port = 0;
    private boolean _autoReconnect = true;
    
    private int _reconnectDelay = Integer.getInteger ( "org.openscada.da.net.client.reconnect_delay", 10 * 1000 );

    public ConnectionInfo ()
    {
    }
    
    public ConnectionInfo ( String hostName, int port )
    {
        super ();
        _hostName = hostName;
        _port = port;
    }
    
    public boolean isValid ()
    {
        if ( _hostName == null )
            return false;
        if ( _hostName.equals ( "" ) )
            return false;
        
        if ( _port <= 0 )
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

    public String getHostName ()
    {
        return _hostName;
    }

    public void setHostName ( String hostName )
    {
        _hostName = hostName;
    }

    public int getPort ()
    {
        return _port;
    }

    public void setPort ( int port )
    {
        _port = port;
    }
    
}
