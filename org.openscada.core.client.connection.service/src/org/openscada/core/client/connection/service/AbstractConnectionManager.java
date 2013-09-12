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
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.utils.osgi.FilterUtil;
import org.eclipse.scada.utils.osgi.SingleServiceListener;
import org.eclipse.scada.utils.osgi.SingleServiceTracker;
import org.openscada.core.client.DriverFactory;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.core.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionManager<ConnectionServiceType extends ConnectionService> implements SingleServiceListener<DriverFactory>
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractConnectionManager.class );

    private final ConnectionInformation connectionInformation;

    private final BundleContext context;

    private final String connectionId;

    private SingleServiceTracker<DriverFactory> tracker;

    private ServiceRegistration<?> serviceReg;

    private DriverFactory factory;

    private AbstractConnectionService connection;

    private final Class<ConnectionServiceType> clazz;

    public AbstractConnectionManager ( final Class<ConnectionServiceType> clazz, final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation )
    {
        super ();
        this.clazz = clazz;
        this.context = context;
        this.connectionId = connectionId;
        this.connectionInformation = connectionInformation;

        final String interfaceName = connectionInformation.getInterface ();
        final String driverName = connectionInformation.getDriver ();

        Filter filter;
        try
        {
            final Map<String, String> parameters = new HashMap<String, String> ();
            parameters.put ( DriverFactory.INTERFACE_NAME, interfaceName );
            parameters.put ( DriverFactory.DRIVER_NAME, driverName );
            filter = FilterUtil.createAndFilter ( DriverFactory.class.getName (), parameters );
        }
        catch ( final InvalidSyntaxException e )
        {
            filter = null;
            logger.warn ( "Failed to create filter", e );
        }

        if ( filter != null )
        {
            this.tracker = new SingleServiceTracker<DriverFactory> ( this.context, filter, this );
            this.tracker.open ();
        }
        else
        {
            this.tracker = null;
        }
    }

    /**
     * Dispose the tracker and the connection
     */
    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
        disposeConnection ();
    }

    @Override
    public void serviceChange ( final ServiceReference<DriverFactory> reference, final DriverFactory factory )
    {
        logger.info ( "Service changed: {}/{}", reference, factory );
        disposeConnection ();
        this.factory = factory;

        if ( factory != null )
        {
            createConnection ();
        }
    }

    /**
     * Create a new connection and register it with OSGi
     */
    protected void createConnection ()
    {
        this.connection = createConnectionService ( this.factory, this.connectionInformation );

        if ( this.connection != null )
        {
            this.connection.connect ();

            final Hashtable<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_PID, this.connectionId );
            properties.put ( DriverFactory.INTERFACE_NAME, this.connectionInformation.getInterface () );
            properties.put ( DriverFactory.DRIVER_NAME, this.connectionInformation.getDriver () );
            logger.info ( "Registered new connection service: {}", properties );
            this.serviceReg = this.context.registerService ( new String[] { this.clazz.getName (), org.openscada.core.connection.provider.ConnectionService.class.getName () }, this.connection, properties );
        }
    }

    protected abstract AbstractConnectionService createConnectionService ( DriverFactory factory, ConnectionInformation connectionInformation );

    /**
     * Revoke the service registration and dispose the service
     * <p>
     * Does nothing if there is no current registration
     */
    protected void disposeConnection ()
    {
        if ( this.serviceReg != null )
        {
            final AbstractConnectionService connection = this.connection;
            connection.disconnect ();
            this.connection = null;

            this.serviceReg.unregister ();
            this.serviceReg = null;

            // now dispose the connection
            connection.dispose ();
        }
    }
}