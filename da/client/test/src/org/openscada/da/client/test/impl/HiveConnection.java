package org.openscada.da.client.test.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.ui.IActionFilter;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ConnectionInfo;
import org.openscada.da.client.net.ConnectionStateListener;
import org.openscada.da.client.net.Connection.State;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.data.Variant;

public class HiveConnection extends Observable implements IActionFilter
{
    private static Logger _log = Logger.getLogger ( HiveConnection.class );
    
    private boolean _connectionRequested = false;
    private HiveConnectionInformation _connectionInfo;
    private Connection _connection = null;
    
    private Map < String, HiveItem > _itemMap = new HashMap < String, HiveItem > ();
    
    private FolderEntry _rootFolder = null;
    
    public HiveConnection ( HiveConnectionInformation connectionInfo )
    {
        _connectionInfo = connectionInfo;
        
        ConnectionInfo conInfo = new ConnectionInfo ();
        conInfo.setHostName ( _connectionInfo.getHost () );
        conInfo.setPort ( connectionInfo.getPort () );
        conInfo.setAutoReconnect ( false );
        
        _connection = new Connection ( conInfo );
        _connection.addConnectionStateListener ( new ConnectionStateListener(){

            public void stateChange ( Connection connection, State state, Throwable error )
            {
                performStateChange ( state, error );
            }
            
            });
        _connection.getItemList().addObserver(new Observer(){
            public void update ( Observable o, Object arg )
            {
                performItemListUpdate();
            }
        });
        
        _rootFolder = new FolderEntry ( "", new HashMap<String, Variant>(), null, this, true );
    }
    
    public void connect ()
    {
        //if ( _connectionRequested )
        //    return;
        
        _connectionRequested = true;
        setChanged ();
        notifyObservers ();
        
        //if ( _connection != null )
        //    return;
        
        _log.debug("Initiating connection...");
        
        try
        {
            _connection.connect ();
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to start connection", e );
            Openscada_da_client_testPlugin.logError ( 1, "Unable to connect", e );
        }
        _log.debug ( "Connection fired up..." );
    }
    
    public void disconnect ()
    {
        _connectionRequested = false;
        
        setChanged ();
        notifyObservers ();
        
        _connection.disconnect ();
    }
    
    public HiveConnectionInformation getConnectionInformation()
    {
        return _connectionInfo;
    }
    
    private void performStateChange ( Connection.State state, Throwable error )
    {
        setChanged ();
        notifyObservers ();
        
        switch ( state )
        {
        case CLOSED:
            _rootFolder.clear ();
            break;
        default:
            break;
        }
        
        if ( error != null )
        {
            Openscada_da_client_testPlugin.getDefault ().notifyError ( "Connection failed", error );
        }
    }
    
    synchronized private void performItemListUpdate ()
    {
        Map<String,HiveItem> items = new HashMap<String,HiveItem> ();
        
        Collection<DataItemInformation> list = _connection.getItemList().getItemList ();
        for ( DataItemInformation item : list )
        {
            if ( _itemMap.containsKey(item) )
                items.put ( item.getName(), _itemMap.get ( item ) );
            else
            {
                items.put ( item.getName(), new HiveItem ( this, item ) );
            }
        }
        
        _itemMap = items;
        
        setChanged();
        notifyObservers();
    }
    
    synchronized public Collection<HiveItem> getItemList ()
    {
        if ( _connection.getState ().equals ( Connection.State.CLOSED ) )
            return new ArrayList<HiveItem>();
        
        return _itemMap.values();
    }

    public Connection getConnection ()
    {
        return _connection;
    }

    public boolean isConnectionRequested ()
    {
        return _connectionRequested;
    }
    
    synchronized public HiveItem lookupItem ( String itemName )
    {
        return _itemMap.get ( itemName );
    }

    public boolean testAttribute ( Object target, String name, String value )
    {
        if ( name.equals ( "state" ) )
        {
            return _connection.getState ().equals ( State.valueOf ( value ) );
        }
        return false;
    }

    public FolderEntry getRootFolder ()
    {
        return _rootFolder;
    }
    
}
