package org.openscada.da.base.connection;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.rcp.da.client.Activator;

public class ConnectionManager
{

    private static class Entry
    {
        Connection connection;

        ItemManager itemManager;
    }

    private final Map<ConnectionInformation, Entry> connections = new HashMap<ConnectionInformation, Entry> ();

    private synchronized Entry getEntry ( final ConnectionInformation ci, final boolean connect )
    {
        Entry entry = this.connections.get ( ci );
        if ( entry == null )
        {
            entry = new Entry ();
            entry.connection = (Connection)Activator.createConnection ( ci );
            entry.itemManager = new ItemManager ( entry.connection );
            if ( connect )
            {
                entry.connection.connect ();
            }
            if ( entry.connection != null )
            {
                this.connections.put ( ci, entry );
            }
        }
        return entry;
    }

    public synchronized DataItemHolder getDataItemHolder ( final ConnectionInformation ci, final String itemId, final boolean connect )
    {
        final Entry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return new DataItemHolder ( entry.connection, entry.itemManager, itemId );
        }
        return null;
    }

    public synchronized Connection getConnection ( final ConnectionInformation ci, final boolean connect )
    {
        final Entry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.connection;
        }
        return null;
    }

    public synchronized Connection getConnection ( final String connectionUri, final boolean connect )
    {
        final Entry entry = getEntry ( ConnectionInformation.fromURI ( connectionUri ), connect );
        if ( entry != null )
        {
            return entry.connection;
        }
        return null;
    }

    public synchronized Connection getConnection ( final URI uri, final boolean connect )
    {
        return getConnection ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public synchronized ItemManager getItemManager ( final ConnectionInformation ci, final boolean connect )
    {
        final Entry entry = getEntry ( ci, connect );
        if ( entry != null )
        {
            return entry.itemManager;
        }
        return null;
    }

    public synchronized ItemManager getItemManager ( final URI uri, final boolean connect )
    {
        return getItemManager ( ConnectionInformation.fromURI ( uri ), connect );
    }

    public synchronized void dispose ()
    {
        for ( final Map.Entry<ConnectionInformation, Entry> entry : this.connections.entrySet () )
        {
            entry.getValue ().connection.disconnect ();
        }
        this.connections.clear ();
    }

    public static ConnectionManager getDefault ()
    {
        return org.openscada.da.base.Activator.getConnectionManager ();
    }
}
