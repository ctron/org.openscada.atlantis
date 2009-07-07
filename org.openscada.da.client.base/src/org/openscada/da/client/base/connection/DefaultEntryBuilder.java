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

import java.util.concurrent.Executors;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.connector.Activator;

/**
 * Build a simple connection
 * @author Jens Reimann
 *
 */
public class DefaultEntryBuilder implements EntryBuilder
{
    public ConnectionManagerEntry build ( final ConnectionInformation connectionInformation, final boolean requireOpen )
    {
        if ( connectionInformation == null )
        {
            return null;
        }

        final ConnectionManagerEntry entry = new ConnectionManagerEntry ();

        final Connection connection = (Connection)Activator.createConnection ( connectionInformation );

        if ( connection == null )
        {
            return null;
        }

        setupConnection ( connection, requireOpen );

        entry.setConnection ( connection );
        entry.setItemManager ( new ItemManager ( entry.getConnection () ) );

        return entry;
    }

    /**
     * configure the new connection
     * @param connection the connection to configure
     * @param requireOpen flag which indicates if the connection should be opened
     */
    protected void setupConnection ( final Connection connection, final boolean requireOpen )
    {
        connection.setExecutor ( Executors.newFixedThreadPool ( 1 ) );
        if ( requireOpen )
        {
            connection.connect ();
        }
    }

}
