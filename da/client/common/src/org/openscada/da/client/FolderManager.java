/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.core.Location;

public class FolderManager implements ConnectionStateListener
{
    protected Connection _connection = null;
    private Map<Location, FolderSyncController> _folderListeners = new HashMap<Location, FolderSyncController> ();
    
    public FolderManager ( Connection connection )
    {
        _connection = connection;
        _connection.addConnectionStateListener ( this );
    }
    
    public void addFolderListener ( FolderListener listener, Location location )
    {
        synchronized ( _folderListeners )
        {
            if ( !_folderListeners.containsKey ( location ) )
            {
                _folderListeners.put ( location, new FolderSyncController ( _connection, new Location ( location ) ) );
            }
            
            FolderSyncController controller = _folderListeners.get ( location );
            controller.addListener ( listener );
        }    
    }
    
    public void addFolderWatcher ( FolderWatcher watcher )
    {
        addFolderListener ( watcher, watcher.getLocation () );
    }
    
    public void removeFolderListener ( FolderListener listener, Location location )
    {
        synchronized ( _folderListeners )
        {
            if ( !_folderListeners.containsKey ( location ) )
            {
                return;
            }
            
            FolderSyncController controller = _folderListeners.get ( location );
            controller.removeListener ( listener );
        }    
    }
    
    public void removeFolderWatcher ( FolderWatcher watcher )
    {
        removeFolderListener ( watcher, watcher.getLocation () );
    }
    
    private void resyncAllFolders ()
    {
        synchronized ( _folderListeners )
        {
            for ( Map.Entry<Location,FolderSyncController> entry : _folderListeners.entrySet () )
            {
                entry.getValue ().resync ();
            }
        }
    }
    
    private void disconnectAllFolders ()
    {
        synchronized ( _folderListeners )
        {
            for ( Map.Entry<Location,FolderSyncController> entry : _folderListeners.entrySet () )
            {
                entry.getValue ().disconnected ();
            }
        }
    }

    public void stateChange ( org.openscada.core.client.Connection connection, ConnectionState state, Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            resyncAllFolders ();
            break;
        case CLOSED:
            disconnectAllFolders ();
            break;
        }
    }
    
}
