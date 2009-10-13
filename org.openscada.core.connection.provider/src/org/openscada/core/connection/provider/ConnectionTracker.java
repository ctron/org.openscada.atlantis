package org.openscada.core.connection.provider;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.core.client.DriverFactory;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionTracker.class );

    private final ConnectionRequest request;

    private final SingleServiceTracker tracker;

    private Filter filter;

    private final Listener listener;

    private ConnectionService service;

    private final BundleContext context;

    private ServiceRegistration handle;

    public interface Listener
    {
        public void setConnection ( final ConnectionService connectionService );
    }

    public ConnectionTracker ( final BundleContext context, final ConnectionRequest request, final Listener listener )
    {
        this.context = context;
        this.request = request;
        this.listener = listener;

        final Map<String, String> parameters = new HashMap<String, String> ();

        // add connection URI to filter criteria so we will only receive connections that match our connection uri
        parameters.put ( ConnectionService.CONNECTION_URI, request.getConnectionInformation ().toString () );

        try
        {
            this.filter = FilterUtil.createAndFilter ( ConnectionService.class.getName (), parameters );
        }
        catch ( final InvalidSyntaxException e )
        {
            logger.warn ( "Failed to create filter", e );
            this.filter = null;
        }

        synchronized ( this )
        {
            if ( this.filter != null )
            {
                this.tracker = new SingleServiceTracker ( context, this.filter, new SingleServiceListener () {

                    public void serviceChange ( final ServiceReference reference, final Object service )
                    {
                        ConnectionTracker.this.setService ( reference, (ConnectionService)service );
                    }
                } );
            }
            else
            {
                this.tracker = null;
            }
        }
    }

    public synchronized void request ()
    {
        if ( this.handle == null )
        {
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( DriverFactory.DRIVER_NAME, this.request.getConnectionInformation ().getDriver () );
            properties.put ( DriverFactory.INTERFACE_NAME, this.request.getConnectionInformation ().getInterface () );
            this.handle = this.context.registerService ( ConnectionRequest.class.getName (), this.request, properties );
        }
    }

    public synchronized void listen ()
    {
        if ( this.tracker != null )
        {
            logger.debug ( "Opening tracker" );
            this.tracker.open ();
        }
    }

    public synchronized void open ()
    {
        listen ();
        request ();
    }

    public synchronized void close ()
    {
        unrequest ();
        unlisten ();
    }

    public synchronized void unlisten ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public synchronized void unrequest ()
    {
        if ( this.handle != null )
        {
            logger.debug ( "Unregister handle: {}", this.handle );
            this.handle.unregister ();
            this.handle = null;
        }
    }

    protected synchronized void setService ( final ServiceReference reference, final ConnectionService service )
    {
        logger.debug ( "Set service: {} -> {}", new Object[] { reference, service } );
        this.service = service;

        if ( this.listener != null )
        {
            this.listener.setConnection ( service );
        }
    }

    public synchronized ConnectionService getService ()
    {
        return this.service;
    }

}
