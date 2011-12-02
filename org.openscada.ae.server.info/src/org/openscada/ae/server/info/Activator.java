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

package org.openscada.ae.server.info;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.info.internal.InfoServiceFactory;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.DataSource;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPool;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator
{
    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.ae.server.info";

    // The shared instance
    private static Activator plugin;

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private ExecutorService executor;

    private ObjectPoolImpl dataSourcePool;

    private InfoServiceFactory factory;

    private ServiceRegistration<ConfigurationFactory> factoryHandle;

    private ServiceRegistration<ObjectPool> dataSourcePoolHandler;

    private ObjectPoolTracker monitorPoolTracker;

    /**
     * The constructor
     */
    public Activator ()
    {
    }

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        plugin = this;

        logger.info ( "Starting up..." );

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        this.monitorPoolTracker = new ObjectPoolTracker ( context, MonitorService.class.getName () );
        this.monitorPoolTracker.open ();

        this.dataSourcePool = new ObjectPoolImpl ();
        this.dataSourcePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.dataSourcePool, DataSource.class.getName () );

        this.factory = new InfoServiceFactory ( context, this.executor, this.monitorPoolTracker, this.dataSourcePool );
        final Dictionary<String, String> properties = new Hashtable<String, String> ( 2 );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A monitor query" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, InfoServiceFactory.FACTORY_ID );

        this.factoryHandle = context.registerService ( ConfigurationFactory.class, this.factory, properties );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factoryHandle.unregister ();
        this.factory.dispose ();
        this.factory = null;

        this.dataSourcePoolHandler.unregister ();

        // shut down executor
        this.executor.shutdown ();

        plugin = null;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }
}
