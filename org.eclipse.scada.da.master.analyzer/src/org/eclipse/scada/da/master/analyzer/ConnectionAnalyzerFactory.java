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

package org.eclipse.scada.da.master.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.core.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionAnalyzerFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionAnalyzerFactory.class );

    private final ServiceTracker<ConnectionService, ConnectionService> connectionTracker;

    private final Map<ConnectionService, ConnectionAnalyzer> serviceMap = new HashMap<ConnectionService, ConnectionAnalyzer> ();

    private final BundleContext context;

    private final ScheduledExecutorService executor;

    public ConnectionAnalyzerFactory ( final ScheduledExecutorService executor, final BundleContext context )
    {
        this.executor = executor;
        this.context = context;
        this.connectionTracker = new ServiceTracker<ConnectionService, ConnectionService> ( context, ConnectionService.class, new ServiceTrackerCustomizer<ConnectionService, ConnectionService> () {

            @Override
            public void removedService ( final ServiceReference<ConnectionService> reference, final ConnectionService service )
            {
                ConnectionAnalyzerFactory.this.removeService ( service );
            }

            @Override
            public void modifiedService ( final ServiceReference<ConnectionService> reference, final ConnectionService service )
            {

            }

            @Override
            public ConnectionService addingService ( final ServiceReference<ConnectionService> reference )
            {
                try
                {
                    logger.debug ( "Found new service: {}", reference );
                    final ConnectionService service = context.getService ( reference );
                    ConnectionAnalyzerFactory.this.addService ( reference, service );
                    return service;
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to add service", e );
                }
                context.ungetService ( reference );
                return null;
            }
        } );
        this.connectionTracker.open ();
    }

    public void dispose ()
    {
        this.connectionTracker.close ();
    }

    protected void addService ( final ServiceReference<ConnectionService> reference, final ConnectionService service )
    {
        logger.info ( "Adding service: {} -> {}", new Object[] { reference, service } );

        final ConnectionAnalyzer analyzer = new ConnectionAnalyzer ( this.executor, this.context, reference, service );
        this.serviceMap.put ( service, analyzer );
    }

    protected void removeService ( final ConnectionService service )
    {
        logger.info ( "Removing service: {}", service );

        final ConnectionAnalyzer analyzer = this.serviceMap.remove ( service );
        if ( analyzer != null )
        {
            analyzer.dispose ();
        }
    }
}
