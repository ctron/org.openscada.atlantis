/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 IBH SYSTEMS GmbH (http://ibh-systems.com)
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

package org.eclipse.scada.da.server.dave;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.scada.ca.ConfigurationAdministrator;
import org.eclipse.scada.ca.ConfigurationFactory;
import org.eclipse.scada.ca.common.factory.BeanConfigurationFactory;
import org.eclipse.scada.da.server.dave.factory.ConfigurationFactoryImpl;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolHelper;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.da.server.common.DataItem;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private ConfigurationFactoryImpl service;

    private BeanConfigurationFactory blockFactory;

    private ExecutorService executor;

    private ObjectPoolImpl<DataItem> itemPool;

    private ServiceRegistration<?> itemPoolHandle;

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
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.eclipse.scada.da.server.dave.device" );
            context.registerService ( ConfigurationFactory.class.getName (), this.service, properties );
        }

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.eclipse.scada.da.server.dave.block" );
            this.blockFactory = new BeanConfigurationFactory ( context, BlockConfiguration.class );
            context.registerService ( ConfigurationFactory.class.getName (), this.blockFactory, properties );
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

        this.executor.shutdown ();
        this.executor = null;
    }

}
