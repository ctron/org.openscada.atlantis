/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.base.connection;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.base.item.DataItemHolder;

public class ConnectionManager
{

    private final Collection<ConnectionManagerListener> listeners = new CopyOnWriteArrayList<ConnectionManagerListener> ();

    private final Map<ConnectionInformation, ConnectionManagerEntry> connections = new HashMap<ConnectionInformation, ConnectionManagerEntry> ();

    private final EntryBuilder builder;

    public ConnectionManager ()
    {
        this.builder = new DefaultEntryBuilder ();
    }

    public ConnectionManager ( final EntryBuilder builder )
    {
        this.builder = builder;
    }

    public Collection<ConnectionManagerEntry> getConnections ()
    {
        return this.connections.values ();
    }

    /**
     * Get the complete entry including item manager
     * @param ci the connection information
     * @param connect <code>true</code> if the connection should be connected when created
     * @return the connection manager entry
     */
    public ConnectionManagerEntry getEntry ( final ConnectionInformation ci, final boolean connect )
    {
        ConnectionManagerEntry entry;

        ConnectionManagerEntry newEntry = null;

        synchronized ( this )
        {
            entry = this.connections.get ( ci );
            if ( entry == null )
            {
                newEntry = entry = this.builder.build ( ci, connect );

                if ( entry != null )
                {
                    this.connections.put ( ci, entry );
                }
            }
        }

        if ( newEntry != null )
        {
            fireConnectionsAdded ( Arrays.asList ( newEntry ) );
        }

        return entry;
    }

    public DataItemHolder getDataItemHolder ( final ConnectionInformation ci, final String itemId, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return new DataItemHolder ( entry.getConnection (), entry.getItemManager (), itemId );
        }
        return null;
    }

    public Connection getConnection ( final ConnectionInformation ci, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.getConnection ();
        }
        return null;
    }

    public Connection getConnection ( final String connectionUri, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ConnectionInformation.fromURI ( connectionUri ), connect );
        if ( entry != null )
        {
            return entry.getConnection ();
        }
        return null;
    }

    public Connection getConnection ( final URI uri, final boolean connect )
    {
        return getConnection ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public ItemManager getItemManager ( final ConnectionInformation ci, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.getItemManager ();
        }
        return null;
    }

    public ItemManager getItemManager ( final URI uri, final boolean connect )
    {
        return getItemManager ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public synchronized void dispose ()
    {
        for ( final Map.Entry<ConnectionInformation, ConnectionManagerEntry> entry : this.connections.entrySet () )
        {
            entry.getValue ().dispose ();
        }
        this.connections.clear ();
    }

    public static ConnectionManager getDefault ()
    {
        return org.openscada.da.client.base.Activator.getConnectionManager ();
    }

    protected void fireConnectionsAdded ( final Collection<ConnectionManagerEntry> connections )
    {
        for ( final ConnectionManagerListener listener : this.listeners )
        {
            listener.connectionsAdded ( connections );
        }
    }

    protected void fireConnectionsRemoved ( final Collection<ConnectionManagerEntry> connections )
    {
        for ( final ConnectionManagerListener listener : this.listeners )
        {
            listener.connectionsRemoved ( connections );
        }
    }

    public void addConnectionManagerListener ( final ConnectionManagerListener listener )
    {
        this.listeners.add ( listener );
    }

    public void removeConnectionManagerListener ( final ConnectionManagerListener listener )
    {
        this.listeners.remove ( listener );
    }
}
