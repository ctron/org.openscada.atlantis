/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.proxy.Hive;
import org.openscada.da.server.proxy.item.ProxyDataItem;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
 *
 */
public class ProxyDataItemFactory implements DataItemFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyDataItemFactory.class );

    private final String separator;

    private final ProxyPrefixName prefix;

    private final ProxyConnection connection;

    private final Hive hive;

    public ProxyDataItemFactory ( final ProxyPrefixName prefix, final ProxyConnection connection, final Hive hive, final String separator )
    {
        this.separator = separator;
        this.prefix = prefix;
        this.connection = connection;
        this.hive = hive;
    }

    @Override
    public boolean canCreate ( final String requestItemId )
    {
        logger.info ( "Checking request: {} for {}", requestItemId, this.prefix );

        return requestItemId.startsWith ( this.prefix.getName () + this.separator );
    }

    @Override
    public void create ( final String requestItemId )
    {
        if ( !canCreate ( requestItemId ) )
        {
            return;
        }

        final ProxyDataItem item = this.connection.realizeItem ( requestItemId );
        this.hive.registerItem ( item );
    }
}
