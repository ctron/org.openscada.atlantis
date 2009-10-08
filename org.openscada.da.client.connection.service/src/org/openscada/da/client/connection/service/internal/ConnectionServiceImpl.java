/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.connection.service.internal;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.connection.service.ConnectionService;

public class ConnectionServiceImpl implements ConnectionService
{
    private final Connection connection;

    private final ItemManager itemManager;

    private final AutoReconnectController controller;

    public ConnectionServiceImpl ( final Connection connection, final ItemManager itemManager )
    {
        this.connection = connection;
        this.itemManager = itemManager;
        this.controller = new AutoReconnectController ( connection );
        this.controller.connect ();
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public void dispose ()
    {
        this.controller.disconnect ();
    }

}
