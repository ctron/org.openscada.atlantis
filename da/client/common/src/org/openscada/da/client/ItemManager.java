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

import org.apache.log4j.Logger;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class ItemManager implements ConnectionStateListener
{
    private static Logger _log = Logger.getLogger ( ItemManager.class );

    protected org.openscada.da.client.Connection _connection = null;

    private Map<String, ItemSyncController> _itemListeners = new HashMap<String, ItemSyncController> ();

    public ItemManager ( org.openscada.da.client.Connection connection )
    {
        super ();
        _connection = connection;
        _connection.addConnectionStateListener ( this );
    }

    public synchronized void addItemUpdateListener ( String itemName, ItemUpdateListener listener )
    {
        if ( !_itemListeners.containsKey ( itemName ) )
        {
            _itemListeners.put ( itemName, new ItemSyncController ( _connection, new String ( itemName ) ) );
        }

        ItemSyncController controller = _itemListeners.get ( itemName );
        controller.add ( listener );
    }

    public synchronized void removeItemUpdateListener ( String itemName, ItemUpdateListener listener )
    {
        if ( !_itemListeners.containsKey ( itemName ) )
        {
            return;
        }

        ItemSyncController controller = _itemListeners.get ( itemName );
        controller.remove ( listener );
    }

    /**
     * Synchronized all items that are currently known
     *
     */
    private synchronized void resyncAllItems ()
    {
        _log.debug ( "Syncing all items" );

        for ( Map.Entry<String, ItemSyncController> entry : _itemListeners.entrySet () )
        {
            entry.getValue ().sync ( true );
        }

        _log.debug ( "re-sync complete" );
    }

    public void stateChange ( org.openscada.core.client.Connection connection, ConnectionState state, Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            resyncAllItems ();
            break;
        default:
            break;
        }
    }

}
