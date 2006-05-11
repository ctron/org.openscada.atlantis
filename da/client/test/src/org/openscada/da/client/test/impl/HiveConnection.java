package org.openscada.da.client.test.impl;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ConnectionInfo;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;

public class HiveConnection
{
    private static Logger _log = Logger.getLogger ( HiveConnection.class );
    
    private HiveConnectionInformation _connectionInfo;
    private Connection _connection = null;
    
    public HiveConnection ( HiveConnectionInformation connectionInfo )
    {
        _connectionInfo = connectionInfo;
    }
    
    synchronized public void connect ()
    {
        if ( _connection != null )
            return;
        
        _log.debug("Initiating connection...");
        
        try
        {
            SocketAddress remote = InetSocketAddress.createUnresolved(_connectionInfo.getHost(),_connectionInfo.getPort());
            _connection = new Connection(new ConnectionInfo(remote));
        }
        catch ( Exception e )
        {
            Openscada_da_client_testPlugin.logError(1,"Unable to connect", e);
        }
    }
    
    synchronized public boolean isConnected ()
    {
        return _connection != null;
    }
    
    public HiveConnectionInformation getConnectionInformation()
    {
        return _connectionInfo;
    }
}
