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

package org.openscada.ae.server.common.monitor.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private BundleContext context;

    private BundleMonitorQuery allQuery;

    private ServiceRegistration handle;

    private ObjectPoolTracker poolTracker;

    private QueryServiceFactory factory;

    private ServiceRegistration factoryHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        logger.info ( "Starting bundle" );

        this.context = context;

        this.poolTracker = new ObjectPoolTracker ( context, MonitorService.class.getName () );
        this.poolTracker.open ();

        this.allQuery = new BundleMonitorQuery ( context, this.poolTracker );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, context.getBundle ().getSymbolicName () + ".all" );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A condition query containing all condition services" );

        this.handle = this.context.registerService ( MonitorQuery.class.getName (), this.allQuery, properties );

        // register factory
        this.factory = new QueryServiceFactory ( context, this.poolTracker );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A monitor query" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, QueryServiceFactory.FACTORY_ID );

        this.factoryHandle = context.registerService ( ConfigurationFactory.class.getName (), this.factory, properties );

        logger.info ( "Initialized" );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factoryHandle.unregister ();
        this.factory.dispose ();
        this.factory = null;

        this.poolTracker.close ();

        this.allQuery.dispose ();
        this.handle.unregister ();
    }

}
