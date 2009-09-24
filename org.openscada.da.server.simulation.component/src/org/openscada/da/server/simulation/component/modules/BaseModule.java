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

package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.component.Hive;

public class BaseModule
{
    private Hive hive = null;

    private String base = null;

    private final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    public BaseModule ( final Hive hive, final String base )
    {
        this.hive = hive;
        this.base = base;
    }

    public void dispose ()
    {
        for ( final DataItem item : this.items.values () )
        {
            this.hive.unregisterItem ( item );
            this.hive.getStorage ().removed ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
        }
        this.items.clear ();
    }

    protected DataItemInputChained getInput ( final String name, final Map<String, Variant> attributes )
    {
        final String id = getItemId ( name );

        final DataItem dataItem = this.items.get ( name );
        if ( dataItem != null )
        {
            if ( dataItem instanceof DataItemInputChained )
            {
                return (DataItemInputChained)dataItem;
            }
            else
            {
                throw new ItemAlreadyRegisteredException ( name );
            }
        }

        final DataItemInputChained item = new DataItemInputChained ( id, this.hive.getOperationService () );
        this.items.put ( name, item );
        this.hive.registerItem ( item );

        final ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        this.hive.getStorage ().added ( idesc );
        return item;
    }

    protected DataItemCommand getOutput ( final String name, final Map<String, Variant> attributes )
    {
        final String id = getItemId ( name );

        final DataItem dataItem = this.items.get ( name );
        if ( dataItem != null )
        {
            if ( dataItem instanceof DataItemCommand )
            {
                return (DataItemCommand)dataItem;
            }
            else
            {
                throw new ItemAlreadyRegisteredException ( name );
            }
        }

        final DataItemCommand item = new DataItemCommand ( id, this.hive.getOperationService () );
        this.items.put ( name, item );
        this.hive.registerItem ( item );

        final ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        this.hive.getStorage ().added ( idesc );
        return item;
    }

    private String getItemId ( final String name )
    {
        return this.base + "." + name;
    }
}
