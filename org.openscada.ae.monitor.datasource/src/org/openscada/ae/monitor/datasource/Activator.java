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

package org.openscada.ae.monitor.datasource;

import org.openscada.ca.ConfigurationAdministrator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
    private static Activator instance;

    private ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> configAdminTracker;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        instance = this;

        this.configAdminTracker = new ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> ( context, ConfigurationAdministrator.class, null );
        this.configAdminTracker.open ();
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.configAdminTracker.close ();

        instance = null;
    }

    public static ConfigurationAdministrator getConfigAdmin ()
    {
        return Activator.instance.configAdminTracker.getService ();
    }
}
