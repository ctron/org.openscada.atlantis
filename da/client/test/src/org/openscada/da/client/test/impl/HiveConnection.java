package org.openscada.da.client.test.impl;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ConnectionInfo;
import org.openscada.da.client.net.ConnectionStateListener;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;

public class HiveConnection extends Observable
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
            InetSocketAddress remote = new InetSocketAddress(_connectionInfo.getHost(),_connectionInfo.getPort());
            
            _connection = new Connection(new ConnectionInfo(remote));
            _connection.addConnectionStateListener(new ConnectionStateListener(){

                public void connected ( Connection arg0 )
                {
                   performConnected();
                }

                public void disconnected ( Connection arg0 )
                {
                    performDisconnected();
                }});
            _connection.getItemList().addObserver(new Observer(){
                public void update ( Observable o, Object arg )
                {
                    performItemListUpdate();
                }
            });
            _connection.start();
        }
        catch ( Exception e )
        {
            Openscada_da_client_testPlugin.logError(1,"Unable to connect", e);
        }
    }
    
    synchronized public boolean isConnected ()
    {
        if ( _connection == null )
            return false;
        
        return _connection.isConnected();
    }
    
    public HiveConnectionInformation getConnectionInformation()
    {
        return _connectionInfo;
    }
    
    private void performConnected ()
    {
        _log.debug("Notify observers");
        setChanged();
        notifyObservers();
    }
    
    private void performDisconnected ()
    {
        setChanged();
        notifyObservers();
    }
    
    private void performItemListUpdate ()
    {
        setChanged();
        notifyObservers();
    }
    
    synchronized public Collection<String> getItemList ()
    {
        if ( !isConnected () )
            return new ArrayList<String>();
        
        return _connection.getItemList().getItemList();
    }
}
