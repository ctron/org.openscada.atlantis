/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
import java.util.concurrent.Executor;

import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemManager implements ConnectionStateListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemManager.class );

    protected org.openscada.da.client.Connection connection;

    private final Map<String, ItemSyncController> itemListeners = new HashMap<String, ItemSyncController> ();

    private boolean connected;

    /**
     * Create a new item manager.
     * <p>
     * Only one item manager should be created per connection since the item manager acquires
     * the listeners for data items using {@link Connection#setItemUpdateListener(String, ItemUpdateListener)}
     * which directs all further updates to this item manager an disconnects all other item managers.
     * So item managers should be shared.
     * </p>
     * @param connection the new connection to use for this item manager
     */
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
        ItemSyncController controller = this.itemListeners.get ( itemName );
        if ( controller == null )
        {
            controller = new ItemSyncController ( this.connection, this, itemName );
            this.itemListeners.put ( itemName, controller );
        }
        controller.add ( listener );
    }

    public synchronized void removeItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
    {
        final ItemSyncController controller = this.itemListeners.get ( itemName );
        if ( itemName == null )
        {
            return;
        }
        controller.remove ( listener );
    }

    /**
     * Synchronized all items that are currently known
     *
     */
    protected synchronized void resyncAllItems ()
    {
        logger.debug ( "Syncing all items" );

        for ( final Map.Entry<String, ItemSyncController> entry : this.itemListeners.entrySet () )
        {
            entry.getValue ().sync ( true );
        }

        logger.debug ( "re-sync complete" );
    }

    protected synchronized void disconnectAllItems ()
    {
        logger.debug ( "Disconnecting all items" );

        for ( final Map.Entry<String, ItemSyncController> entry : this.itemListeners.entrySet () )
        {
            entry.getValue ().disconnect ();
        }

        logger.debug ( "Disconnecting all items: complete" );
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
