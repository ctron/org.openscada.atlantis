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

package org.openscada.ca.server.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.server.Service;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private SingleServiceTracker<ConfigurationAdministrator> tracker;

    private Service service;

    private ServiceRegistration<Service> handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;
        this.tracker = new SingleServiceTracker<ConfigurationAdministrator> ( bundleContext, ConfigurationAdministrator.class, new SingleServiceListener<ConfigurationAdministrator> () {

            @Override
            public void serviceChange ( final ServiceReference<ConfigurationAdministrator> reference, final ConfigurationAdministrator service )
            {
                setService ( service );
            }
        } );
        this.tracker.open ();
    }

    protected void setService ( final ConfigurationAdministrator service )
    {
        if ( this.handle != null )
        {
            this.handle.unregister ();
            this.handle = null;

        }
        if ( this.service != null )
        {
            try
            {
                this.service.stop ();
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to stop service", e );
            }
            this.service = null;
        }

        if ( service != null )
        {
            try
            {
                final ServiceImpl newService = new ServiceImpl ( service, context );

                newService.start ();
                this.service = newService;

                final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
                properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
                properties.put ( Constants.SERVICE_DESCRIPTION, "openSCADA CA Server Interface" );
                this.handle = context.registerService ( Service.class, this.service, properties );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to start new service", e );
            }

        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.tracker.close ();
        Activator.context = null;
    }

}
