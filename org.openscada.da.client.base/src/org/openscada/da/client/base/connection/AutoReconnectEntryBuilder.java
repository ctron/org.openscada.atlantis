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
import org.openscada.core.client.AutoReconnectController;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.connector.Activator;

public class AutoReconnectEntryBuilder implements EntryBuilder
{
    public static class Entry extends ConnectionManagerEntry
    {
        private AutoReconnectController controller;

        public AutoReconnectController getController ()
        {
            return this.controller;
        }

        public void setController ( final AutoReconnectController controller )
        {
            this.controller = controller;
        }

        @Override
        public void dispose ()
        {
            this.controller.disconnect ();
            super.dispose ();
        }
    }

    public ConnectionManagerEntry build ( final ConnectionInformation connectionInformation, final boolean requireOpen )
    {
        if ( connectionInformation == null )
        {
            return null;
        }

        final Entry entry = new Entry ();

        final Connection connection = (Connection)Activator.createConnection ( connectionInformation );

        if ( connection == null )
        {
            return null;
        }

        entry.setConnection ( connection );
        entry.setItemManager ( new ItemManager ( connection ) );
        entry.setController ( new AutoReconnectController ( connection ) );

        setupConnection ( entry, requireOpen );

        return entry;
    }

    /**
     * configure the new connection
     * @param connection the connection to configure
     * @param requireOpen flag which indicates if the connection should be opened
     */
    protected void setupConnection ( final Entry entry, final boolean requireOpen )
    {
        entry.getConnection ().setExecutor ( Executors.newFixedThreadPool ( 1 ) );

        if ( requireOpen )
        {
            entry.getController ().connect ();
        }
    }
}
