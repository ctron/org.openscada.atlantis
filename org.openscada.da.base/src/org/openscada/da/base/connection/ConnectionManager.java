/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.base.connection;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.rcp.da.client.Activator;

public class ConnectionManager
{

    private final Collection<ConnectionManagerListener> listeners = new CopyOnWriteArrayList<ConnectionManagerListener> ();

    private final Map<ConnectionInformation, ConnectionManagerEntry> connections = new HashMap<ConnectionInformation, ConnectionManagerEntry> ();

    public Collection<ConnectionManagerEntry> getConnections ()
    {
        return this.connections.values ();
    }

    public synchronized ConnectionManagerEntry getEntry ( final ConnectionInformation ci, final boolean connect )
    {
        ConnectionManagerEntry entry = this.connections.get ( ci );
        if ( entry == null )
        {
            entry = new ConnectionManagerEntry ();
            entry.setConnection ( (Connection)Activator.createConnection ( ci ) );
            entry.setItemManager ( new ItemManager ( entry.getConnection () ) );
            if ( connect )
            {
                entry.getConnection ().connect ();
            }
            if ( entry.getConnection () != null )
            {
                this.connections.put ( ci, entry );
            }
            fireConnectionsAdded ( Arrays.asList ( entry ) );
        }
        return entry;
    }

    public synchronized DataItemHolder getDataItemHolder ( final ConnectionInformation ci, final String itemId, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return new DataItemHolder ( entry.getConnection (), entry.getItemManager (), itemId );
        }
        return null;
    }

    public synchronized Connection getConnection ( final ConnectionInformation ci, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.getConnection ();
        }
        return null;
    }

    public synchronized Connection getConnection ( final String connectionUri, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ConnectionInformation.fromURI ( connectionUri ), connect );
        if ( entry != null )
        {
            return entry.getConnection ();
        }
        return null;
    }

    public synchronized Connection getConnection ( final URI uri, final boolean connect )
    {
        return getConnection ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public synchronized ItemManager getItemManager ( final ConnectionInformation ci, final boolean connect )
    {
        final ConnectionManagerEntry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.getItemManager ();
        }
        return null;
    }

    public synchronized ItemManager getItemManager ( final URI uri, final boolean connect )
    {
        return getItemManager ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public synchronized void dispose ()
    {
        for ( final Map.Entry<ConnectionInformation, ConnectionManagerEntry> entry : this.connections.entrySet () )
        {
            entry.getValue ().getConnection ().disconnect ();
        }
        this.connections.clear ();
    }

    public static ConnectionManager getDefault ()
    {
        return org.openscada.da.base.Activator.getConnectionManager ();
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
