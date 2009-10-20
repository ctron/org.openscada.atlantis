/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.ice;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * An OSGi bundle activator which registers the DriverAdapter with OSGi
 * @author Jens Reimann
 * @since 0.14.0
 *
 */
public class Activator implements BundleActivator
{
    private org.openscada.core.client.DriverFactory factory;

    private ServiceRegistration handle;

    public void start ( final BundleContext context ) throws Exception
    {
        this.factory = new DriverFactory ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( org.openscada.core.client.DriverFactory.INTERFACE_NAME, "da" );
        properties.put ( org.openscada.core.client.DriverFactory.DRIVER_NAME, "ice" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "OpenSCADA DA ICE Adapter" );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        this.handle = context.registerService ( org.openscada.core.client.DriverFactory.class.getName (), this.factory, properties );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.factory = null;
    }

}
