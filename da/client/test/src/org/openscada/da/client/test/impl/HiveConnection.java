/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.ui.IActionFilter;
import org.openscada.core.Variant;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.ConnectionState;
import org.openscada.da.client.Connection;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;

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
        
        _connection = new org.openscada.da.client.net.Connection ( conInfo );
        _connection.addConnectionStateListener ( new ConnectionStateListener(){

            public void stateChange ( org.openscada.core.client.Connection connection, ConnectionState state, Throwable error )
            {
                performStateChange ( state, error );
            }

        });

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
        
        _log.debug ( "Initiating connection..." );
        
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
    
    private synchronized void performStateChange ( ConnectionState state, Throwable error )
    {
        _log.debug ( String.format ( "State Change to %s (%s)", state, error ) );
        
        switch ( state )
        {
        case BOUND:
            _rootFolder = new FolderEntry ( "", new HashMap<String, Variant>(), null, this, true );
            break;
        case CLOSED:
            if ( _rootFolder != null )
            {
                _rootFolder.dispose ();
                _rootFolder = null;
            }
            break;
        default:
            break;
        }
        
        setChanged ();
        notifyObservers ();
        
        if ( error != null )
        {
            Openscada_da_client_testPlugin.getDefault ().notifyError ( "Connection failed", error );
        }
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
            return _connection.getState ().equals ( ConnectionState.valueOf ( value ) );
        }
        return false;
    }

    public FolderEntry getRootFolder ()
    {
        return _rootFolder;
    }
    
    public void notifyFolderChange ( FolderEntry folder )
    {
        setChanged ();
        notifyObservers ( folder );
    }
    
}
