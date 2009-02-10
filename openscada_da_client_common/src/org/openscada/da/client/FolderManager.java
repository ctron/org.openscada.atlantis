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
    protected Connection connection = null;

    private final Map<Location, FolderSyncController> folderListeners = new HashMap<Location, FolderSyncController> ();

    public FolderManager ( final Connection connection )
    {
        this.connection = connection;
        this.connection.addConnectionStateListener ( this );
    }

    public void dispose ()
    {
        this.connection.removeConnectionStateListener ( this );
        disconnectAllFolders ();
        this.connection = null;
    }

    public void addFolderListener ( final FolderListener listener, final Location location )
    {
        synchronized ( this.folderListeners )
        {
            if ( !this.folderListeners.containsKey ( location ) )
            {
                this.folderListeners.put ( location, new FolderSyncController ( this.connection, new Location ( location ) ) );
            }

            final FolderSyncController controller = this.folderListeners.get ( location );
            controller.addListener ( listener );
        }
    }

    public void addFolderWatcher ( final FolderWatcher watcher )
    {
        addFolderListener ( watcher, watcher.getLocation () );
    }

    public void removeFolderListener ( final FolderListener listener, final Location location )
    {
        synchronized ( this.folderListeners )
        {
            final FolderSyncController controller = this.folderListeners.get ( location );
            if ( controller == null )
            {
                return;
            }
            controller.removeListener ( listener );
        }
    }

    public void removeFolderWatcher ( final FolderWatcher watcher )
    {
        removeFolderListener ( watcher, watcher.getLocation () );
    }

    private void resyncAllFolders ()
    {
        synchronized ( this.folderListeners )
        {
            for ( final Map.Entry<Location, FolderSyncController> entry : this.folderListeners.entrySet () )
            {
                entry.getValue ().resync ();
            }
        }
    }

    private void disconnectAllFolders ()
    {
        synchronized ( this.folderListeners )
        {
            for ( final Map.Entry<Location, FolderSyncController> entry : this.folderListeners.entrySet () )
            {
                entry.getValue ().disconnected ();
            }
        }
    }

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
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
