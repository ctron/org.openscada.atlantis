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

package org.openscada.da.server.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openscada.da.client.ItemManager;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;

/**
 * uses a redundant connection to provide failover redundancy
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public class ProxyDataItemFactory implements DataItemFactory
{
    Map<String, SubConnection> connections = new HashMap<String, SubConnection> ();

    Map<String, ItemManager> itemManagers = new HashMap<String, ItemManager> ();

    private String separator = ".";

    /**
     * @param connection
     */
    public ProxyDataItemFactory ( final Map<String, SubConnection> connections, final String separator )
    {
        this.separator = separator;
        this.connections.putAll ( connections );
        for ( final Entry<String, SubConnection> entry : connections.entrySet () )
        {
            this.itemManagers.put ( entry.getKey (), new ItemManager ( entry.getValue ().getConnection () ) );
        }
    }

    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        for ( final String prefix : this.connections.keySet () )
        {
            if ( request.getId ().startsWith ( prefix + this.separator ) )
            {
                return true;
            }
        }
        return false;
    }

    public DataItem create ( final DataItemFactoryRequest request )
    {
        if ( request == null )
        {
            return null;
        }
        for ( final Entry<String, SubConnection> entry : this.connections.entrySet () )
        {
            if ( request.getId ().startsWith ( entry.getKey () + this.separator ) )
            {
                final ItemManager itemManager = this.itemManagers.get ( entry.getKey () );
                final ProxyItem item = new ProxyItem ( entry.getValue ().getConnection (), request.getId (), entry.getKey () + this.separator );
                itemManager.addItemUpdateListener ( request.getId (), item );
                return item;
            }
        }
        return null;
    }
}