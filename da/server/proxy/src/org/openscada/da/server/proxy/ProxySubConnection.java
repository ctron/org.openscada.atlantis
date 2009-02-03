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

package org.openscada.da.server.proxy;

import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxySubConnection
{
    private final Connection connection;

    private final ItemManager itemManager;

    private final ProxyPrefixName prefix;

    private final ProxySubConnectionId id;

    private final FolderManager folderManager;

    /**
     * @param connection
     * @param id
     * @param prefix
     */
    public ProxySubConnection ( final Connection connection, final ProxySubConnectionId id, final ProxyPrefixName prefix )
    {
        this.connection = connection;
        this.itemManager = new ItemManager ( this.connection );
        this.folderManager = new FolderManager ( this.connection );
        this.prefix = prefix;
        this.id = id;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    /**
     * @return actual connection
     */
    public Connection getConnection ()
    {
        return this.connection;
    }

    /**
     * @return item prefix which should be substituted
     */
    public ProxyPrefixName getPrefix ()
    {
        return this.prefix;
    }

    /**
     * @return id of subconnection
     */
    public ProxySubConnectionId getId ()
    {
        return this.id;
    }

    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }
}
