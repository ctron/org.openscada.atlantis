/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ae.sec.AuthorizationHelper;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private DataItemTargetFactoryImpl factory;

    private ExecutorService executor;

    private DataItemSourceFactoryImpl factory2;

    private AuthorizationHelper authorization;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.authorization = new AuthorizationHelper ( context );
        this.authorization.open ();

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        {
            this.factory = new DataItemTargetFactoryImpl ( context, this.authorization );
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
    public void stop ( final BundleContext context ) throws Exception
    {

        this.factory.dispose ();
        this.factory2.dispose ();

        this.executor.shutdown ();

        this.authorization.close ();

    }

}
