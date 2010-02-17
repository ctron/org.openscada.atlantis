/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class ItemManager implements ConnectionStateListener
{
    private static Logger log = Logger.getLogger ( ItemManager.class );

    protected org.openscada.da.client.Connection connection = null;

    private final Map<String, ItemSyncController> itemListeners = new HashMap<String, ItemSyncController> ();

    private boolean connected = false;

    public ItemManager ( final org.openscada.da.client.Connection connection )
    {
        super ();
        this.connection = connection;

        synchronized ( this )
        {
            this.connection.addConnectionStateListener ( this );
            this.connected = this.connection.getState () == ConnectionState.BOUND;
        }
    }

    public Executor getExecutor ()
    {
        // we don't cache the executor since it might change on the connection
        return this.connection.getExecutor ();
    }

    /**
     * Get the current assigned connection
     * @return the current connection or <code>null</code> if none is assigned.
     */
    public Connection getConnection ()
    {
        return this.connection;
    }

    public synchronized void addItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
    {
        if ( !this.itemListeners.containsKey ( itemName ) )
        {
            this.itemListeners.put ( itemName, new ItemSyncController ( this.connection, this, itemName ) );
        }

        final ItemSyncController controller = this.itemListeners.get ( itemName );
        controller.add ( listener );
    }

    public synchronized void removeItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
    {
        if ( !this.itemListeners.containsKey ( itemName ) )
        {
            return;
        }

        final ItemSyncController controller = this.itemListeners.get ( itemName );
        controller.remove ( listener );
    }

    /**
     * Synchronized all items that are currently known
     *
     */
    protected synchronized void resyncAllItems ()
    {
        log.debug ( "Syncing all items" );

        for ( final Map.Entry<String, ItemSyncController> entry : this.itemListeners.entrySet () )
        {
            entry.getValue ().sync ( true );
        }

        log.debug ( "re-sync complete" );
    }

    protected synchronized void disconnectAllItems ()
    {
        log.debug ( "Disconnecting all items" );

        for ( final Map.Entry<String, ItemSyncController> entry : this.itemListeners.entrySet () )
        {
            entry.getValue ().disconnect ();
        }

        log.debug ( "Disconnecting all items: complete" );
    }

    public synchronized void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            if ( !this.connected )
            {
                resyncAllItems ();
                this.connected = true;
            }
            break;
        default:
            if ( this.connected )
            {
                disconnectAllItems ();
                this.connected = false;
            }
            break;
        }
    }

}
