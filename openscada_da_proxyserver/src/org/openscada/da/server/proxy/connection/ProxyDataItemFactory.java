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

package org.openscada.da.server.proxy.connection;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyDataItemFactory implements DataItemFactory
{
    private final static Logger logger = Logger.getLogger ( ProxyDataItemFactory.class );

    private final String separator;

    private final ProxyPrefixName prefix;

    private final ProxyConnection connection;

    public ProxyDataItemFactory ( final ProxyPrefixName prefix, final ProxyConnection connection, final String separator )
    {
        this.separator = separator;
        this.prefix = prefix;
        this.connection = connection;
    }

    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        final String requestItemId = request.getId ();

        if ( logger.isInfoEnabled () )
        {
            logger.info ( String.format ( "Checking request: %s for %s", requestItemId, this.prefix ) );
        }

        return requestItemId.startsWith ( this.prefix.getName () + this.separator );
    }

    public DataItem create ( final DataItemFactoryRequest request )
    {
        if ( !canCreate ( request ) )
        {
            return null;
        }

        return this.connection.realizeItem ( request.getId () );
    }
}
