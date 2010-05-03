package org.openscada.ae.server.http;

import javax.servlet.ServletException;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.server.http.internal.JsonServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator
{
    private static final String SERVLET_PATH = "/org.openscada.ae";

    private BundleContext context;

    private EventProcessor eventProcessor;

    private ServiceTracker httpServiceTracker;

    private HttpService httpService;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.eventProcessor = new EventProcessor ( context );
        this.httpServiceTracker = new ServiceTracker ( context, HttpService.class.getName (), createHttpServiceTrackerCustomizer () );

        this.eventProcessor.open ();
        this.httpServiceTracker.open ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.httpServiceTracker.close ();
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
            this.httpService.registerServlet ( SERVLET_PATH, new JsonServlet ( this.eventProcessor ), null, null );
            this.httpService.registerResources ( SERVLET_PATH + "/ui", "/ui", null );
        }
        catch ( ServletException e )
        {
            e.printStackTrace ();
        }
        catch ( NamespaceException e )
        {
            e.printStackTrace ();
        }
    }

    private void unbind ()
    {
        if ( this.httpService == null )
        {
            return;
        }
        this.httpService.unregister ( SERVLET_PATH + "/ui" );
        this.httpService.unregister ( SERVLET_PATH );
    }

    private ServiceTrackerCustomizer createHttpServiceTrackerCustomizer ()
    {
        return new ServiceTrackerCustomizer () {
            public Object addingService ( final ServiceReference reference )
            {
                Object service = Activator.this.context.getService ( reference );
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

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                // pass
            }

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
