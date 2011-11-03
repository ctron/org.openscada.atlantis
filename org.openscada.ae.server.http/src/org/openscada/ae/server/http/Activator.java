/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.http;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ae.server.http.internal.JsonServlet;
import org.openscada.ae.server.http.monitor.EventMonitorFactory;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator
{
    private static final String SERVLET_PATH = "/org.openscada.ae";

    private BundleContext context;

    private ExecutorService executor;

    private EventProcessor eventProcessor;

    private ServiceTracker httpServiceTracker;

    private HttpService httpService;

    private ServiceRegistration factoryServiceHandle;

    private EventMonitorFactory factory;

    private ObjectPoolImpl monitorServicePool;

    private ServiceRegistration monitorServicePoolHandler;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        this.eventProcessor = new EventProcessor ( context );

        this.monitorServicePool = new ObjectPoolImpl ();
        this.monitorServicePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.monitorServicePool, MonitorService.class.getName () );

        this.httpServiceTracker = new ServiceTracker ( context, HttpService.class.getName (), createHttpServiceTrackerCustomizer () );

        this.eventProcessor.open ();

        // register factory
        this.factory = new EventMonitorFactory ( this.context, this.executor, this.monitorServicePool, this.eventProcessor );
        final Hashtable<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, EventMonitorFactory.FACTORY_ID );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Reference list alarm monitor" );
        this.factoryServiceHandle = this.context.registerService ( new String[] { ConfigurationFactory.class.getName (), AknHandler.class.getName () }, this.factory, properties );

        this.httpServiceTracker.open ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        // do not process any events anymore
        this.httpServiceTracker.close ();

        // remove factory
        if ( this.factoryServiceHandle != null )
        {
            this.factoryServiceHandle.unregister ();
        }
        if ( this.factory != null )
        {
            this.factory.dispose ();
        }

        // shut down event processor
        this.eventProcessor.close ();

        // shut down object pool
        this.monitorServicePoolHandler.unregister ();
        this.monitorServicePool.dispose ();

        // shut down executor
        this.executor.shutdown ();

        this.context = null;
    }

    private void bind ()
    {
        if ( this.httpService == null )
        {
            return;
        }
        try
        {
            // register servlet
            this.httpService.registerServlet ( SERVLET_PATH, new JsonServlet ( this.eventProcessor, this.factory ), null, null );
            this.httpService.registerResources ( SERVLET_PATH + "/ui", "/ui", null );
        }
        catch ( final ServletException e )
        {
            e.printStackTrace ();
        }
        catch ( final NamespaceException e )
        {
            e.printStackTrace ();
        }
    }

    private void unbind ()
    {
        if ( this.httpService != null )
        {
            this.httpService.unregister ( SERVLET_PATH + "/ui" );
            this.httpService.unregister ( SERVLET_PATH );
        }
    }

    private ServiceTrackerCustomizer createHttpServiceTrackerCustomizer ()
    {
        return new ServiceTrackerCustomizer () {
            @Override
            public Object addingService ( final ServiceReference reference )
            {
                final Object service = Activator.this.context.getService ( reference );
                synchronized ( Activator.this )
                {
                    if ( Activator.this.httpService == null )
                    {
                        Activator.this.httpService = (HttpService)service;
                        Activator.this.bind ();
                    }
                }
                return service;
            }

            @Override
            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                // pass
            }

            @Override
            public void removedService ( final ServiceReference reference, final Object service )
            {
                synchronized ( Activator.this )
                {
                    if ( service != Activator.this.httpService )
                    {
                        return;
                    }
                    Activator.this.unbind ();
                    Activator.this.bind ();
                }
            }
        };
    }
}
