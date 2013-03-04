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

package org.openscada.da.master.mapper;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.mapper.ValueMapper;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private ObjectPoolTracker<MasterItem> poolTracker;

    private MapperHandlerFactoryImpl factory;

    private ObjectPoolTracker<ValueMapper> mapperPoolTracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.poolTracker = new ObjectPoolTracker<MasterItem> ( context, MasterItem.class );
        this.poolTracker.open ();

        this.mapperPoolTracker = new ObjectPoolTracker<ValueMapper> ( context, ValueMapper.class );
        this.mapperPoolTracker.open ();

        this.factory = new MapperHandlerFactoryImpl ( context, this.poolTracker, this.mapperPoolTracker, 1001 /* after manual */);
        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "A value mapper master handler" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, MapperHandlerFactoryImpl.FACTORY_ID );
        context.registerService ( ConfigurationFactory.class.getName (), this.factory, properties );

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {

        this.factory.dispose ();
        this.factory = null;

        this.poolTracker.close ();
        this.poolTracker = null;
    }
}
