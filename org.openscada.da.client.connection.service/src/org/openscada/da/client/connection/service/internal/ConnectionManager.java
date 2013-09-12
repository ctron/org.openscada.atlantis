/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.connection.service.internal;

import org.eclipse.scada.core.ConnectionInformation;
import org.openscada.core.client.DriverFactory;
import org.openscada.core.client.connection.service.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.da.client.Connection;
import org.openscada.da.connection.provider.ConnectionService;
import org.openscada.da.connection.provider.ConnectionServiceImpl;
import org.osgi.framework.BundleContext;

public class ConnectionManager extends AbstractConnectionManager<ConnectionService>
{
    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation )
    {
        super ( ConnectionService.class, context, connectionId, connectionInformation );
    }

    @Override
    protected AbstractConnectionService createConnectionService ( final DriverFactory factory, final ConnectionInformation connectionInformation )
    {
        final Connection connection = (Connection)factory.getDriverInformation ( connectionInformation ).create ( connectionInformation );
        if ( connection == null )
        {
            return null;
        }
        return new ConnectionServiceImpl ( connection, 10000, false );
    }

}
