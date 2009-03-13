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

package org.openscada.da.client.base.item;

import org.eclipse.ui.IMemento;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.base.connection.ConnectionManager;
import org.openscada.da.client.base.connection.ConnectionManagerEntry;

public class DataItemHolder
{

    private Connection connection;

    private ItemManager itemManager;

    private String itemId;

    /**
     * Create a new data item holder from values
     * @param connection the connection
     * @param itemManager the associated item manager
     * @param itemId the data item id
     */
    public DataItemHolder ( final Connection connection, final ItemManager itemManager, final String itemId )
    {
        super ();
        this.connection = connection;
        this.itemManager = itemManager;
        this.itemId = itemId;
    }

    /**
     * Create a new data item holder from an {@link IMemento}
     * @param memento the memento to use. Must not be <code>null</code>!
     * @param connectionManager the connection manager to use
     */
    public DataItemHolder ( final IMemento memento, final ConnectionManager connectionManager )
    {
        super ();

        final String connectionUri = memento.getString ( "connectionUri" );
        final String itemId = memento.getString ( "itemId" );

        final ConnectionManagerEntry entry = connectionManager.getEntry ( ConnectionInformation.fromURI ( connectionUri ), false );

        this.itemId = itemId;
        this.connection = entry.getConnection ();
        this.itemManager = entry.getItemManager ();
    }

    /**
     * Load a data item from a memento
     * @param memento the memento to load from. May be <code>null</code>
     * @return the loaded item holder or <code>null</code> it the item cannot be initialized
     */
    public static DataItemHolder loadFrom ( final IMemento memento )
    {
        if ( memento != null )
        {
            try
            {
                return new DataItemHolder ( memento, ConnectionManager.getDefault () );
            }
            catch ( final Throwable e )
            {
                return null;
            }
        }
        return null;
    }

    public void saveTo ( final IMemento memento )
    {
        if ( memento == null )
        {
            // nothing to do
            return;
        }

        memento.putString ( "itemId", this.itemId );
        memento.putString ( "connectionUri", this.connection.getConnectionInformation ().toString () );
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    public String getItemId ()
    {
        return this.itemId;
    }

    public void setItemId ( final String itemId )
    {
        this.itemId = itemId;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public void setItemManager ( final ItemManager itemManager )
    {
        this.itemManager = itemManager;
    }

}
