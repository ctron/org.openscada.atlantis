/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
                this.connection.getExecutor ().execute ( new Runnable () {

                    public void run ()
                    {
                        entry.getValue ().disconnected ();
                    }
                } );
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
