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

package org.openscada.core.client.connection.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.sec.UserInformation;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.connection.provider.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractManagedConnectionServiceFactory<ConnectionServiceType extends ConnectionService> implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractManagedConnectionServiceFactory.class );

    public static final String CONNECTION_URI = "connection.uri";

    private final Map<String, AbstractConnectionManager<ConnectionServiceType>> connections = new HashMap<String, AbstractConnectionManager<ConnectionServiceType>> ();

    @Override
    public void delete ( final UserInformation userInformation, final String pid )
    {
        logger.info ( "Request to delete: {}", pid ); //$NON-NLS-1$

        AbstractConnectionManager<ConnectionServiceType> connection;
        synchronized ( this )
        {
            connection = this.connections.remove ( pid );
        }
        if ( connection != null )
        {
            connection.dispose ();
        }
    }

    @Override
    public void update ( final UserInformation userInformation, final String pid, final Map<String, String> properties )
    {
        logger.info ( "Update request: {} ({})", pid, properties ); //$NON-NLS-1$

        final String uri = properties.get ( CONNECTION_URI );

        if ( uri == null )
        {
            throw new RuntimeException ( String.format ( "'%s' is not set", CONNECTION_URI ) ); //$NON-NLS-1$
        }

        synchronized ( this )
        {
            // delete first
            delete ( userInformation, pid );

            // create
            final AbstractConnectionManager<ConnectionServiceType> newManager = createConnectionManager ( pid, uri );
            if ( newManager != null )
            {
                publishConnection ( pid, newManager );
            }
        }
    }

    protected abstract AbstractConnectionManager<ConnectionServiceType> createConnectionManager ( String pid, String uri );

    private void publishConnection ( final String pid, final AbstractConnectionManager<ConnectionServiceType> manager )
    {
        this.connections.put ( pid, manager );
    }

    public void dispose ()
    {
        final Map<String, AbstractConnectionManager<ConnectionServiceType>> connections;

        synchronized ( this )
        {
            connections = new HashMap<String, AbstractConnectionManager<ConnectionServiceType>> ( this.connections );
            this.connections.clear ();
        }
        for ( final AbstractConnectionManager<ConnectionServiceType> manager : connections.values () )
        {
            manager.dispose ();
        }

    }
}