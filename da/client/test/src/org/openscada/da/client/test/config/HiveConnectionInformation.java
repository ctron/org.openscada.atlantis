package org.openscada.da.client.test.config;

import java.io.Serializable;

public class HiveConnectionInformation implements Serializable
{
    /**
     * Serializeable ID 
     */
    private static final long serialVersionUID = 4226848319802862860L;
    
    private String _host;
    private int _port;
    private boolean _autoReconnect;
    
    public HiveConnectionInformation ()
    {
        _host = "";
        _port = 0;
        _autoReconnect = false;
    }
    
    public String getHost ()
    {
        return _host;
    }
    
    public void setHost ( String host )
    {
        _host = host;
    }
    
    public int getPort ()
    {
        return _port;
    }
    
    public void setPort ( int port )
    {
        _port = port;
    }

    public boolean isAutoReconnect ()
    {
        return _autoReconnect;
    }

    public void setAutoReconnect ( boolean autoReconnect )
    {
        _autoReconnect = autoReconnect;
    }
}
