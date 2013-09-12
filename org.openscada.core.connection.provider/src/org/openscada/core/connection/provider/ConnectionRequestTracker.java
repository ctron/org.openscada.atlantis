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

package org.openscada.core.connection.provider;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.scada.core.ConnectionInformation;
import org.openscada.core.client.DriverFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionRequestTracker extends ConnectionTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionRequestTracker.class );

    private final ConnectionRequest request;

    private ServiceRegistration<ConnectionRequest> handle;

    private final BundleContext context;

    public ConnectionRequestTracker ( final BundleContext context, final ConnectionRequest request, final Listener listener )
    {
        this ( context, request, listener, null );
    }

    public ConnectionRequestTracker ( final BundleContext context, final ConnectionRequest request, final Listener listener, final Class<? extends ConnectionService> clazz )
    {
        super ( context, listener, clazz );
        this.context = context;
        this.request = request;
    }

    @Override
    protected Map<String, String> createFilterParameters ()
    {
        final Map<String, String> parameters = new HashMap<String, String> ();

        // add connection URI to filter criteria so we will only receive connections that match our connection uri
        parameters.put ( ConnectionService.CONNECTION_URI, this.request.getConnectionInformation ().toString () );

        if ( this.request.getRequestId () != null )
        {
            parameters.put ( Constants.SERVICE_PID, this.request.getRequestId () );
        }

        return parameters;
    }

    public synchronized void request ()
    {
        if ( this.handle == null && this.request != null )
        {
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( DriverFactory.DRIVER_NAME, this.request.getConnectionInformation ().getDriver () );
            properties.put ( DriverFactory.INTERFACE_NAME, this.request.getConnectionInformation ().getInterface () );
            this.handle = this.context.registerService ( ConnectionRequest.class, this.request, properties );
        }
    }

    public synchronized void unrequest ()
    {
        if ( this.handle != null )
        {
            logger.debug ( "Unregister handle: {}", this.handle );
            this.handle.unregister ();
            this.handle = null;
        }
    }

    @Override
    public synchronized void open ()
    {
        super.open ();
        request ();
    }

    @Override
    public synchronized void close ()
    {
        unrequest ();
        super.close ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.request.getConnectionInformation ();
    }
}
