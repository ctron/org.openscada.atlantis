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

package org.openscada.hd.exporter.http.server;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.scada.utils.osgi.SingleServiceListener;
import org.eclipse.scada.utils.osgi.SingleServiceTracker;
import org.openscada.hd.exporter.http.HttpExporter;
import org.openscada.hd.exporter.http.server.internal.JsonServlet;
import org.openscada.hd.exporter.http.server.internal.LocalHttpExporter;
import org.openscada.hd.exporter.http.server.internal.RemoteHttpExporter;
import org.openscada.hd.server.Service;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{
    private static final String SERVLET_PATH = "/org.openscada.hd";

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private final SingleServiceListener<HttpService> httpServiceListener = new SingleServiceListener<HttpService> () {
        @Override
        public void serviceChange ( final ServiceReference<HttpService> reference, final HttpService service )
        {
            if ( Activator.this.httpService != null )
            {
                Activator.this.httpService.unregister ( SERVLET_PATH );
                Activator.this.httpService.unregister ( "/media" );
                Activator.this.httpService = null;
            }
            Activator.this.httpService = service;
            try
            {
                Activator.this.httpService.registerServlet ( SERVLET_PATH, Activator.this.jsonServlet, null, null );
                Activator.this.httpService.registerResources ( SERVLET_PATH + "/ui", "/ui", null );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to handle http service change", e );
            }
        }
    };

    private final SingleServiceListener<HttpExporter> exporterServiceListener = new SingleServiceListener<HttpExporter> () {
        @Override
        public void serviceChange ( final ServiceReference<HttpExporter> reference, final HttpExporter service )
        {
            Activator.this.jsonServlet.setExporter ( service );
        }
    };

    private final SingleServiceListener<Service> localHdServerServiceListener = new SingleServiceListener<Service> () {
        @Override
        public void serviceChange ( final ServiceReference<Service> reference, final Service service )
        {
            if ( Activator.this.localHdServerServiceRegistration != null )
            {
                Activator.this.localHdServerServiceRegistration.unregister ();
            }
            if ( service != null )
            {
                final Dictionary<String, Object> props = new Hashtable<String, Object> ();
                props.put ( Constants.SERVICE_RANKING, 20 );
                try
                {
                    Activator.this.localHdServerServiceRegistration = context.registerService ( HttpExporter.class, new LocalHttpExporter ( service ), props );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to handle service change", e );
                }
            }
        }
    };

    private final JsonServlet jsonServlet = new JsonServlet ();

    private static BundleContext context;

    private HttpService httpService = null;

    private SingleServiceTracker<HttpService> httpServiceTracker;

    private SingleServiceTracker<HttpExporter> exporterServiceTracker;

    private SingleServiceTracker<Service> localHdServerServiceTracker;

    private ServiceRegistration<HttpExporter> localHdServerServiceRegistration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        Activator.context = context;

        // start servlet
        this.httpServiceTracker = new SingleServiceTracker<HttpService> ( context, HttpService.class, this.httpServiceListener );
        this.httpServiceTracker.open ();

        this.exporterServiceTracker = new SingleServiceTracker<HttpExporter> ( context, HttpExporter.class, this.exporterServiceListener );
        this.exporterServiceTracker.open ();

        this.localHdServerServiceTracker = new SingleServiceTracker<Service> ( context, Service.class, this.localHdServerServiceListener );
        this.localHdServerServiceTracker.open ();

        // try to start local exporter
        registerRemoteExporter ( context );
    }

    private void registerRemoteExporter ( final BundleContext context )
    {
        // TODO: create clientConnection
        final Dictionary<String, Object> props = new Hashtable<String, Object> ( 1 );
        props.put ( Constants.SERVICE_RANKING, 10 );
        context.registerService ( HttpExporter.class.getName (), new RemoteHttpExporter (), props );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.httpServiceTracker.close ();
        this.httpServiceTracker = null;
        this.exporterServiceTracker.close ();
        this.exporterServiceTracker = null;
        if ( this.localHdServerServiceRegistration != null )
        {
            this.localHdServerServiceRegistration.unregister ();
        }

        Activator.context = null;
    }
}
