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

package org.openscada.da.base.item;

import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;

public class DataItemHolder
{

    private Connection connection;

    private ItemManager itemManager;
    
    private String itemId;

    public DataItemHolder ( Connection connection, ItemManager itemManager, String itemId )
    {
        super ();
        this.connection = connection;
        this.itemManager = itemManager;
        this.itemId = itemId;
    }

    public Connection getConnection ()
    {
        return connection;
    }

    public void setConnection ( Connection connection )
    {
        this.connection = connection;
    }

    public String getItemId ()
    {
        return itemId;
    }

    public void setItemId ( String itemId )
    {
        this.itemId = itemId;
    }

    public ItemManager getItemManager ()
    {
        return itemManager;
    }

    public void setItemManager ( ItemManager itemManager )
    {
        this.itemManager = itemManager;
    }

}
