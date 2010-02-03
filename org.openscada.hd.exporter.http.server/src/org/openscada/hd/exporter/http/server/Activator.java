package org.openscada.hd.exporter.http.server;

import java.util.Properties;

import org.openscada.hd.exporter.http.HttpExporter;
import org.openscada.hd.exporter.http.server.internal.JsonServlet;
import org.openscada.hd.exporter.http.server.internal.LocalHttpExporter;
import org.openscada.hd.exporter.http.server.internal.RemoteHttpExporter;
import org.openscada.hd.server.Service;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

public class Activator implements BundleActivator
{
    private static final String SERVLET_PATH = "/org.openscada.hd";

    private final SingleServiceListener httpServiceListener = new SingleServiceListener () {
        public void serviceChange ( final ServiceReference reference, final Object service )
        {
            HttpService httpService = (HttpService)service;
            if ( Activator.this.httpService != null )
            {
                Activator.this.httpService.unregister ( SERVLET_PATH );
                Activator.this.httpService.unregister ( "/media" );
                Activator.this.httpService = null;
            }
            Activator.this.httpService = httpService;
            try
            {
                Activator.this.httpService.registerServlet ( SERVLET_PATH, Activator.this.jsonServlet, null, null );
                Activator.this.httpService.registerResources ( SERVLET_PATH + "/ui", "/ui", null );
            }
            catch ( Exception e )
            {
                e.printStackTrace ();
            }
        }
    };

    private final SingleServiceListener exporterServiceListener = new SingleServiceListener () {
        public void serviceChange ( final ServiceReference reference, final Object service )
        {
            HttpExporter exporter = (HttpExporter)service;
            Activator.this.jsonServlet.setExporter ( exporter );
        }
    };

    private final SingleServiceListener localHdServerServiceListener = new SingleServiceListener () {
        public void serviceChange ( final ServiceReference reference, final Object service )
        {
            if ( Activator.this.localHdServerServiceRegistration != null )
            {
                Activator.this.localHdServerServiceRegistration.unregister ();
            }
            Service hdService = (Service)service;
            if ( hdService != null )
            {
                Properties props = new Properties ();
                props.put ( Constants.SERVICE_RANKING, 20 );
                Activator.this.localHdServerServiceRegistration = context.registerService ( HttpExporter.class.getName (), new LocalHttpExporter (), props );
            }
        }
    };

    private final JsonServlet jsonServlet = new JsonServlet ();

    private static BundleContext context;

    private HttpService httpService = null;

    private SingleServiceTracker httpServiceTracker;

    private SingleServiceTracker exporterServiceTracker;

    private SingleServiceTracker localHdServerServiceTracker;

    private ServiceRegistration localHdServerServiceRegistration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        Activator.context = context;

        // start servlet
        this.httpServiceTracker = new SingleServiceTracker ( context, HttpService.class.getName (), this.httpServiceListener );
        this.httpServiceTracker.open ();

        this.exporterServiceTracker = new SingleServiceTracker ( context, HttpExporter.class.getName (), this.exporterServiceListener );
        this.exporterServiceTracker.open ();

        this.localHdServerServiceTracker = new SingleServiceTracker ( context, Service.class.getName (), this.localHdServerServiceListener );
        this.localHdServerServiceTracker.open ();

        // try to start local exporter
        registerRemoteExporter ( context );
    }

    private void registerRemoteExporter ( final BundleContext context )
    {
        // TODO: create clientConnection
        Properties props = new Properties ();
        props.put ( Constants.SERVICE_RANKING, 10 );
        context.registerService ( HttpExporter.class.getName (), new RemoteHttpExporter (), props );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
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
