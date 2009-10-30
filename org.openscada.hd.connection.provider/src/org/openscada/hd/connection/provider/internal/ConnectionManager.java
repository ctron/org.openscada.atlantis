/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.hd.connection.provider.internal;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.hd.client.Connection;
import org.openscada.hd.connection.provider.ConnectionServiceImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager extends AbstractConnectionManager
{
    private static final Logger logger = LoggerFactory.getLogger ( ConnectionManager.class );

    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        super ( context, connectionInformation, connectionId, autoReconnectDelay, initialOpen );
    }

    protected AbstractConnectionService createConnection ()
    {
        logger.debug ( "Create new HD connection: {}", getConnectionInformation () );

        final Connection connection = (Connection)getFactory ().getDriverInformation ( getConnectionInformation () ).create ( getConnectionInformation () );

        if ( connection == null )
        {
            logger.warn ( "Failed to create new HD connection: {}", getConnectionInformation () );
            return null;
        }

        return new ConnectionServiceImpl ( connection, getAutoReconnectDelay () );
    }

}
