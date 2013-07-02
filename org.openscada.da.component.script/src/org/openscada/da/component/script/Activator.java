/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.component.script;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private ScriptComponentFactory factory;

    private ServiceRegistration<ConfigurationFactory> handle;

    private ExecutorService executor;

    private ObjectPoolImpl<DataItem> objectPool;

    private ServiceRegistration<?> poolHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( bundleContext.getBundle ().getSymbolicName () ) );

        this.objectPool = new ObjectPoolImpl<DataItem> ();

        this.factory = new ScriptComponentFactory ( this.executor, this.objectPool, bundleContext );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 2 );

        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, bundleContext.getBundle ().getSymbolicName () );

        this.handle = bundleContext.registerService ( ConfigurationFactory.class, this.factory, properties );

        // register late in order to reduce events
        this.poolHandle = ObjectPoolHelper.registerObjectPool ( bundleContext, this.objectPool, DataItem.class );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.executor.shutdown ();
        this.handle.unregister ();
        this.factory.dispose ();

        this.poolHandle.unregister ();
        this.objectPool.dispose ();

        Activator.context = null;
    }

}
