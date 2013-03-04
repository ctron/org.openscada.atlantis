/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.component.script;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.component.script.ScriptContext.Item;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.Constants;

public class ItemWrapper implements Item
{

    private final DataItemInputChained dataItem;

    private final ObjectPoolImpl<DataItem> objectPool;

    public ItemWrapper ( final ObjectPoolImpl<DataItem> objectPool, final DataItemInputChained dataItem, final Map<String, Variant> attributes )
    {
        this.dataItem = dataItem;
        this.objectPool = objectPool;

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();

        // not quite all attributes, be we tried
        if ( attributes.containsKey ( "description" ) )
        {
            properties.put ( Constants.SERVICE_DESCRIPTION, attributes.get ( "description" ).asString ( "" ) );
        }

        this.objectPool.addService ( dataItem.getInformation ().getName (), dataItem, properties );
    }

    @Override
    public String getItemId ()
    {
        return this.dataItem.getInformation ().getName ();
    }

    @Override
    public void updateData ( final Variant value, final Map<String, Variant> attributes, final AttributeMode attributeMode )
    {
        this.dataItem.updateData ( value, attributes, attributeMode );
    }

    @Override
    public void dispose ()
    {
        this.objectPool.removeService ( this.dataItem.getInformation ().getName (), this.dataItem );
    }

}
