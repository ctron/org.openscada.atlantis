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

package org.openscada.ae.connection.provider;

import org.openscada.ae.client.Connection;
import org.openscada.ae.client.EventManager;
import org.openscada.ae.client.MonitorManager;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.core.connection.provider.info.ConnectionInformationProvider;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final EventManager eventManager;

    private final MonitorManager monitorManager;

    private final Connection connection;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        super ( autoReconnectController, false );
        setConnection ( this.connection = connection );
        this.eventManager = new EventManager ( connection );
        this.monitorManager = new MonitorManager ( connection );
    }

    @Override
    public org.openscada.ae.client.Connection getConnection ()
    {
        return this.connection;
    }

    @Override
    public Class<?>[] getSupportedInterfaces ()
    {
        return new Class<?>[] { org.openscada.core.connection.provider.ConnectionService.class, ConnectionService.class, ConnectionInformationProvider.class };
    }

    @Override
    public EventManager getEventManager ()
    {
        return this.eventManager;
    }

    @Override
    public MonitorManager getMonitorManager ()
    {
        return this.monitorManager;
    }
}
