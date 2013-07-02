/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.ngp;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * An OSGi bundle activator which registers the DriverAdapter with OSGi
 * 
 * @author Jens Reimann
 * @since 1.0.0
 */
public class Activator implements BundleActivator
{
    private org.openscada.core.client.DriverFactory factory;

    private ServiceRegistration<org.openscada.core.client.DriverFactory> handle;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.factory = new DriverFactoryImpl ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( org.openscada.core.client.DriverFactory.INTERFACE_NAME, "da" );
        properties.put ( org.openscada.core.client.DriverFactory.DRIVER_NAME, "ngp" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "OpenSCADA DA NGP Adapter" );
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        this.handle = context.registerService ( org.openscada.core.client.DriverFactory.class, this.factory, properties );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.factory = null;
    }

}
