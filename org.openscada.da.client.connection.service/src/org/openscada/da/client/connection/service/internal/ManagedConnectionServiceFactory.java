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

package org.openscada.da.client.connection.service.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.ConnectionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedConnectionServiceFactory implements ConfigurationFactory
{
    public static final String CONNECTION_URI = "connection.uri";

    private final static Logger logger = LoggerFactory.getLogger ( ManagedConnectionServiceFactory.class );

    private final Map<String, ConnectionManager> connections = new HashMap<String, ConnectionManager> ();

    private final Map<String, ServiceRegistration<ConnectionManager>> connectionsRegs = new HashMap<String, ServiceRegistration<ConnectionManager>> ();

    private final BundleContext context;

    public ManagedConnectionServiceFactory ( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public void delete ( final String pid )
    {
        logger.info ( "Request to delete: {}", pid ); //$NON-NLS-1$

        ServiceRegistration<ConnectionManager> reg;
        ConnectionManager connection;
        synchronized ( this )
        {
            reg = this.connectionsRegs.remove ( pid );
            connection = this.connections.remove ( pid );
        }

        if ( reg != null )
        {
            reg.unregister ();
        }
        if ( connection != null )
        {
            connection.dispose ();
        }
    }

    @Override
    public void update ( final String pid, final Map<String, String> properties )
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
            delete ( pid );

            // create
            final ConnectionManager newManager = new ConnectionManager ( this.context, pid, ConnectionInformation.fromURI ( uri ) );
            publishConnection ( pid, newManager );
        }
    }

    private void publishConnection ( final String pid, final ConnectionManager manager )
    {
        final Dictionary<String, String> regProperties = new Hashtable<String, String> ();
        regProperties.put ( Constants.SERVICE_PID, pid );
        final ServiceRegistration<ConnectionManager> reg = this.context.registerService ( ConnectionManager.class, manager, regProperties );
        this.connectionsRegs.put ( pid, reg );

        this.connections.put ( pid, manager );
    }

    public void dispose ()
    {
        Map<String, ServiceRegistration<ConnectionManager>> connectionsRegs;
        Map<String, ConnectionManager> connections;

        synchronized ( this )
        {
            connectionsRegs = new HashMap<String, ServiceRegistration<ConnectionManager>> ( this.connectionsRegs );
            connections = new HashMap<String, ConnectionManager> ( this.connections );
            this.connectionsRegs.clear ();
            this.connections.clear ();
        }

        for ( final ServiceRegistration<ConnectionManager> reg : connectionsRegs.values () )
        {
            reg.unregister ();
        }
        for ( final ConnectionManager manager : connections.values () )
        {
            manager.dispose ();
        }

    }
}
