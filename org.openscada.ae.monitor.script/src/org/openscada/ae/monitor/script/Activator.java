/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.monitor.script;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.concurrent.ExportedExecutorService;
import org.openscada.utils.interner.InternerHelper;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.collect.Interner;

public class Activator implements BundleActivator
{

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private Interner<String> stringInterner;

    private ScriptMonitorFactory factory;

    private ExecutorService executor;

    private EventProcessor eventProcessor;

    private ObjectPoolTracker<DataSource> dataSourcePoolTracker;

    private ObjectPoolTracker<MasterItem> masterItemPoolTracker;

    private ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> configAdminTracker;

    private ObjectPoolImpl<MonitorService> monitorServicePool;

    private ServiceRegistration<?> monitorServicePoolHandler;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.stringInterner = InternerHelper.makeInterner ( "org.openscada.ae.monitor.datasource.common.stringInternerType", "strong" );

        this.executor = new ExportedExecutorService ( bundleContext.getBundle ().getSymbolicName (), 1, 1, 1, TimeUnit.MINUTES );

        this.eventProcessor = new EventProcessor ( context );
        this.eventProcessor.open ();

        this.dataSourcePoolTracker = new ObjectPoolTracker<DataSource> ( context, DataSource.class );
        this.dataSourcePoolTracker.open ();

        this.masterItemPoolTracker = new ObjectPoolTracker<MasterItem> ( context, MasterItem.class );
        this.masterItemPoolTracker.open ();

        this.configAdminTracker = new ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> ( context, ConfigurationAdministrator.class, null );
        this.configAdminTracker.open ();

        this.monitorServicePool = new ObjectPoolImpl<MonitorService> ();
        this.monitorServicePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.monitorServicePool, MonitorService.class );

        this.factory = new ScriptMonitorFactory ( bundleContext, this.executor, this.stringInterner, this.eventProcessor, this.dataSourcePoolTracker, this.masterItemPoolTracker, this.configAdminTracker, this.monitorServicePool );

        final Hashtable<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, ScriptMonitorFactory.FACTORY_ID );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Script monitor" );
        context.registerService ( new String[] { ConfigurationFactory.class.getName () }, this.factory, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.factory.dispose ();

        this.monitorServicePoolHandler.unregister ();
        this.monitorServicePool.dispose ();

        this.dataSourcePoolTracker.close ();
        this.masterItemPoolTracker.close ();

        this.configAdminTracker.close ();

        this.eventProcessor.close ();
        this.executor.shutdown ();

        Activator.context = null;
    }

}
