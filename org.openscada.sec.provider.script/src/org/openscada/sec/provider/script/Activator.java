/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.sec.provider.script;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.AuthorizationService;
import org.openscada.utils.concurrent.ScheduledExportedExecutorService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private ScriptAuthorizationProvider service;

    private ScheduledExecutorService executor;

    private ServiceRegistration<?> handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = new ScheduledExportedExecutorService ( "org.openscada.sec.provider.script", 1 );
        this.service = new ScriptAuthorizationProvider ( this.executor );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();

        properties.put ( Constants.SERVICE_DESCRIPTION, "A script based authorization service" );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, context.getBundle ().getSymbolicName () + ".factory" );

        this.handle = context.registerService ( new String[] { AuthorizationService.class.getName (), ConfigurationFactory.class.getName () }, this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();

        this.executor.shutdown ();

        this.service = null;
    }

}
