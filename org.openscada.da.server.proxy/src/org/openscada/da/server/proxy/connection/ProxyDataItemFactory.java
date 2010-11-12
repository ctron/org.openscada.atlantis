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

package org.openscada.da.server.proxy.connection;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
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
