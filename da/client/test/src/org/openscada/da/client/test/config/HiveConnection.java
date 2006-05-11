package org.openscada.da.client.test.config;

import java.io.Serializable;

public class HiveConnection implements Serializable
{
    /**
     * Serializeable ID 
     */
    private static final long serialVersionUID = 4226848319802862860L;
    
    private String _host;
    private short _port;
    
    public HiveConnection ()
    {
        _host = "";
        _port = 0;
    }
    
    public String getHost ()
    {
        return _host;
    }
    public void setHost ( String host )
    {
        _host = host;
    }
    public short getPort ()
    {
        return _port;
    }
    public void setPort ( short port )
    {
        _port = port;
    }
}
