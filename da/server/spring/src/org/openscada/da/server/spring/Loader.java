/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.ItemStorage;
import org.openscada.da.server.common.DataItem;

public class Loader
{
    protected String _itemPrefix;
    protected Hive _hive;
    protected Collection<ItemStorage> _storages;

    public void setHive ( Hive hive )
    {
        this._hive = hive;
    }
    
    public void setStorages ( Collection<ItemStorage> storages )
    {
        this._storages = storages;
    }
    
    public void setItemPrefix ( String itemPrefix )
    {
        _itemPrefix = itemPrefix;
    }
    
    public void injectItem ( DataItem item )
    {
        injectItem ( item, new HashMap<String, Variant> () );
    }
    
    public void injectItem ( DataItem item, Map<String, Variant> attributes )
    {
        injectItem ( _hive, _storages, item, attributes );
    }
    
    protected static void injectItem ( Hive hive, Collection<ItemStorage> storages, DataItem item, Map<String, Variant> attributes )
    {
        hive.registerItem ( item );
        for ( ItemStorage storage : storages )
        {
            storage.added ( new ItemDescriptor ( item, attributes ) );
        }        
    }

}
