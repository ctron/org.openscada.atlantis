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

package org.openscada.da.server.dave;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.dave.data.VariableManager;
import org.openscada.da.server.dave.data.VariableManagerImpl;
import org.openscada.da.server.dave.factory.ConfigurationFactoryImpl;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.ca.factory.BeanConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private ConfigurationFactoryImpl service;

    private BeanConfigurationFactory blockFactory;

    private static VariableManagerImpl variableManager;

    private ExecutorService executor;

    private ObjectPoolImpl<DataItem> itemPool;

    private ServiceRegistration<?> itemPoolHandle;

    public static VariableManager getVariableManager ()
    {
        return variableManager;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.itemPool = new ObjectPoolImpl<DataItem> ();

        this.itemPoolHandle = ObjectPoolHelper.registerObjectPool ( context, this.itemPool, DataItem.class );

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        this.service = new ConfigurationFactoryImpl ( context );

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.dave.device" );
            context.registerService ( ConfigurationFactory.class.getName (), this.service, properties );
        }

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.dave.block" );
            this.blockFactory = new BeanConfigurationFactory ( context, BlockConfiguration.class );
            context.registerService ( ConfigurationFactory.class.getName (), this.blockFactory, properties );
        }

        {
            Activator.variableManager = new VariableManagerImpl ( this.executor, this.itemPool );
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.dave.types" );
            context.registerService ( ConfigurationFactory.class.getName (), Activator.variableManager, properties );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.itemPoolHandle.unregister ();
        this.itemPool.dispose ();

        this.blockFactory.dispose ();
        this.service.dispose ();

        Activator.variableManager.dispose ();
        Activator.variableManager = null;

        this.executor.shutdown ();
        this.executor = null;
    }

}
