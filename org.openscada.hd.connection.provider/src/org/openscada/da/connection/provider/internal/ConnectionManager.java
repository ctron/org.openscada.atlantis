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

package org.openscada.da.connection.provider.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.DriverFactory;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.openscada.da.client.Connection;
import org.openscada.hd.connection.provider.ConnectionService;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager implements SingleServiceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionManager.class );

    private final ConnectionInformation connectionInformation;

    private final BundleContext context;

    private final String connectionId;

    private SingleServiceTracker tracker;

    private ConnectionServiceImpl connection;

    private ServiceRegistration serviceReg;

    private DriverFactory factory;

    private final Integer autoReconnectDelay;

    private final boolean initialOpen;

    private final Set<ConnectionRequest> requests = new HashSet<ConnectionRequest> ();

    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation info, final Integer autoReconnectDelay, final boolean isInitialOpen )
    {
        this.context = context;

        this.connectionInformation = info;
        this.connectionId = connectionId;
        this.autoReconnectDelay = autoReconnectDelay;
        this.initialOpen = isInitialOpen;

        final String interfaceName = this.connectionInformation.getInterface ();
        final String driverName = this.connectionInformation.getDriver ();

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
            this.tracker = new SingleServiceTracker ( this.context, filter, this );
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

    public void update ( final ConnectionInformation connectionInformation )
    {
        // FIXME: implement
    }

    public void serviceChange ( final ServiceReference reference, final Object factory )
    {
        logger.info ( "Service changed: " + reference + "/" + factory );

        disposeConnection ();

        this.factory = (DriverFactory)factory;

        if ( this.factory != null )
        {
            createConnection ();
        }
    }

    /**
     * Create a new connection and register it with OSGi
     */
    private void createConnection ()
    {
        final Connection connection = (Connection)this.factory.getDriverInformation ( this.connectionInformation ).create ( this.connectionInformation );

        if ( connection != null )
        {
            this.connection = new ConnectionServiceImpl ( connection, this.autoReconnectDelay );

            if ( this.initialOpen )
            {
                logger.debug ( "Initially open" );
                this.connection.connect ();
            }

            final Hashtable<String, String> properties = new Hashtable<String, String> ();
            if ( this.connectionId != null )
            {
                properties.put ( Constants.SERVICE_PID, this.connectionId );
            }
            properties.put ( DriverFactory.INTERFACE_NAME, this.connectionInformation.getInterface () );
            properties.put ( DriverFactory.DRIVER_NAME, this.connectionInformation.getDriver () );
            properties.put ( ConnectionService.CONNECTION_URI, this.connectionInformation.toString () );

            logger.info ( "Registered new connection service: " + properties );
            this.serviceReg = this.context.registerService ( new String[] { ConnectionService.class.getName (), org.openscada.core.connection.provider.ConnectionService.class.getName () }, this.connection, properties );
        }
    }

    /**
     * Revoke the service registration and dispose the service
     * <p>
     * Does nothing if there is no current registration
     */
    private void disposeConnection ()
    {
        if ( this.serviceReg != null )
        {
            final ConnectionServiceImpl connection = this.connection;
            this.connection = null;

            this.serviceReg.unregister ();
            this.serviceReg = null;

            // now dispose the connection
            connection.dispose ();
        }
    }

    public void addRequest ( final ConnectionRequest request )
    {
        this.requests.add ( request );
    }

    public void removeRequest ( final ConnectionRequest request )
    {
        this.requests.remove ( request );
    }

    public boolean isIdle ()
    {
        return this.requests.isEmpty ();
    }

}
