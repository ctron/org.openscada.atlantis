/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.file;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.file.internal.ConfigurationAdminImpl;
import org.openscada.utils.interner.InternerHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Interner;

public class Activator implements BundleActivator
{
    private AbstractConfigurationAdministrator service;

    private ServiceRegistration<?> handle;

    private Interner<String> stringInterner;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.stringInterner = InternerHelper.makeInterner ( "org.openscada.ca.file.stringInternerType", "weak" );

        this.service = new ConfigurationAdminImpl ( context, this.stringInterner );

        this.service.start ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An openSCADA CA File Implementation" );

        this.handle = context.registerService ( new String[] { ConfigurationAdministrator.class.getName (), FreezableConfigurationAdministrator.class.getName () }, this.service, properties );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.handle = null;

        this.service.dispose ();
        this.service = null;
    }

}
