/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.hd.server.common;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.hd.server.Service;
import org.eclipse.scada.hd.server.common.internal.ServiceImpl;
import org.eclipse.scada.utils.concurrent.ExportedExecutorService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * This activator registers the common OSGi based
 * implementation of the HD server side service
 * 
 * @author Jens Reimann
 * @since 0.14.0
 */
public class Activator implements BundleActivator
{
    private ServiceImpl service;

    private ServiceRegistration<Service> serviceRegistration;

    private ExportedExecutorService executor;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = new ExportedExecutorService ( "org.eclipse.scada.hd.server.common", 1, 1, 1, TimeUnit.MINUTES );
        this.service = new ServiceImpl ( context, this.executor );
        this.service.start ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An openSCADA HD service implementation" );
        this.serviceRegistration = context.registerService ( Service.class, this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.serviceRegistration.unregister ();

        this.service.stop ();
        this.service = null;

        this.executor.shutdown ();
    }

}
