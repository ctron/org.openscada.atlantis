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

package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private DataItemTargetFactoryImpl factory;

    private ExecutorService executor;

    private DataItemSourceFactoryImpl factory2;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        {
            this.factory = new DataItemTargetFactoryImpl ( context );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, DataItemTargetFactoryImpl.FACTORY_ID );
            properties.put ( Constants.SERVICE_DESCRIPTION, "A dataitem based on a datasource" );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory, properties );
        }

        {
            this.factory2 = new DataItemSourceFactoryImpl ( context, this.executor );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, DataItemSourceFactoryImpl.FACTORY_ID );
            properties.put ( Constants.SERVICE_DESCRIPTION, "A datasource based on a dataitem" );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory2, properties );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factory.dispose ();
        this.factory2.dispose ();

        this.executor.shutdown ();
    }

}
