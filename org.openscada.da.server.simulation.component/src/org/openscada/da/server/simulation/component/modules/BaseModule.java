/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
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
