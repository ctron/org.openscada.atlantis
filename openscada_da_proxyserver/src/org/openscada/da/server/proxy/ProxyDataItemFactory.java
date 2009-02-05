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

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyDataItemFactory implements DataItemFactory
{
    final Map<ProxyPrefixName, ProxyConnection> connections = new HashMap<ProxyPrefixName, ProxyConnection> ();

    private String separator = ".";

    /**
     * @param connections
     * @param separator
     */
    public ProxyDataItemFactory ( final Map<ProxyPrefixName, ProxyConnection> connections, final String separator )
    {
        this.separator = separator;
        this.connections.putAll ( connections );
    }

    @Override
    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        return findConnection ( request ) != null;
    }

    protected ProxyConnection findConnection ( final DataItemFactoryRequest request )
    {
        if ( request == null )
        {
            return null;
        }

        for ( final Map.Entry<ProxyPrefixName, ProxyConnection> entry : this.connections.entrySet () )
        {
            if ( request.getId ().startsWith ( entry.getKey ().getName () + this.separator ) )
            {
                return entry.getValue ();
            }
        }
        return null;
    }

    @Override
    public DataItem create ( final DataItemFactoryRequest request )
    {
        final ProxyConnection connection = findConnection ( request );

        if ( connection == null )
        {
            return null;
        }

        final ProxyDataItem item = connection.realizeItem ( request.getId () );

        return item;
    }
}
