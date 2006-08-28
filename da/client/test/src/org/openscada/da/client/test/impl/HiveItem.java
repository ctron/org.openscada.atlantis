/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.test.impl;

import org.openscada.da.core.server.DataItemInformation;

public class HiveItem
{
    private DataItemInformation _itemInfo = null;
    private HiveConnection _connection = null;
    
    public HiveItem ( HiveConnection connection, DataItemInformation itemInfo )
    {
        _connection = connection;
        _itemInfo = itemInfo;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    public DataItemInformation getItemInfo ()
    {
        return _itemInfo;
    }
    
    public String getItemName ()
    {
        return _itemInfo.getName ();
    }
    
    
}
