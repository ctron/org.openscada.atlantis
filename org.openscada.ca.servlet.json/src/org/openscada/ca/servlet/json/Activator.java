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

package org.openscada.ca.servlet.json;

import javax.servlet.ServletException;

import org.openscada.ca.ConfigurationAdministrator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator
{

    private BundleContext context;

    private ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> configurationAdminTracker;

    private ServiceTracker<HttpService, HttpService> httpServiceTracker;

    private ConfigurationAdministrator configurationAdmin;

    private HttpService httpService;

    private HttpContext httpContext;

    private JsonServlet servlet;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;

        final ServiceTrackerCustomizer<ConfigurationAdministrator, ConfigurationAdministrator> configurationAdminCustomizer = createConfigurationAdminCustomizer ();
        this.configurationAdminTracker = new ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> ( context, ConfigurationAdministrator.class, configurationAdminCustomizer );
        final ServiceTrackerCustomizer<HttpService, HttpService> httpServiceCustomizer = createHttpServiceCustomizer ();
        this.httpServiceTracker = new ServiceTracker<HttpService, HttpService> ( context, HttpService.class, httpServiceCustomizer );

        this.configurationAdminTracker.open ();
        this.httpServiceTracker.open ();
    }

    private ServiceTrackerCustomizer<ConfigurationAdministrator, ConfigurationAdministrator> createConfigurationAdminCustomizer ()
    {
        return new ServiceTrackerCustomizer<ConfigurationAdministrator, ConfigurationAdministrator> () {
            @Override
            public ConfigurationAdministrator addingService ( final ServiceReference<ConfigurationAdministrator> reference )
            {
                final ConfigurationAdministrator service = Activator.this.context.getService ( reference );
                synchronized ( Activator.this )
                {
                    if ( Activator.this.configurationAdmin == null )
                    {
                        Activator.this.configurationAdmin = service;
                        Activator.this.bind ();
                    }
                }
                return service;
            }

            @Override
            public void modifiedService ( final ServiceReference<ConfigurationAdministrator> reference, final ConfigurationAdministrator service )
            {
                // pass
            }

            @Override
            public void removedService ( final ServiceReference<ConfigurationAdministrator> reference, final ConfigurationAdministrator service )
            {
                synchronized ( Activator.this )
                {
                    if ( Activator.this.configurationAdmin != service )
                    {
                        return;
                    }
                    Activator.this.unbind ();
                    Activator.this.configurationAdmin = null;
                    Activator.this.bind ();
                }
            }
        };
    }

    private ServiceTrackerCustomizer<HttpService, HttpService> createHttpServiceCustomizer ()
    {
        return new ServiceTrackerCustomizer<HttpService, HttpService> () {
            @Override
            public HttpService addingService ( final ServiceReference<HttpService> reference )
            {
                final HttpService service = Activator.this.context.getService ( reference );
                synchronized ( Activator.this )
                {
                    if ( Activator.this.httpService == null )
                    {
                        Activator.this.httpService = service;
                        Activator.this.bind ();
                    }
                }
                return service;
            }

            @Override
            public void modifiedService ( final ServiceReference<HttpService> reference, final HttpService service )
            {
                // pass
            }

            @Override
            public void removedService ( final ServiceReference<HttpService> reference, final HttpService service )
            {
                synchronized ( Activator.this )
                {
                    if ( Activator.this.httpService != service )
                    {
                        return;
                    }
                    Activator.this.unbind ();
                    Activator.this.httpService = null;
                    Activator.this.bind ();
                }
            }
        };
    }

    private void bind ()
    {
        if ( this.httpService != null && this.configurationAdmin != null )
        {
            this.httpContext = this.httpService.createDefaultHttpContext ();
            try
            {
                this.httpService.registerServlet ( "/org.openscada.ca", this.servlet = new JsonServlet ( this.configurationAdmin ), null, this.httpContext );
            }
            catch ( final NamespaceException e )
            {
                e.printStackTrace ();
            }
            catch ( final ServletException e )
            {
                e.printStackTrace ();
            }
        }
    }

    private void unbind ()
    {
        if ( this.httpService != null )
        {
            this.httpService.unregister ( "/org.openscada.ca" );

            this.servlet.destroy ();
            this.servlet = null;

            this.httpContext = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.httpServiceTracker.close ();
        this.configurationAdminTracker.close ();
    }
}
