package org.openscada.da.connection.provider.internal;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider implements ServiceTrackerCustomizer
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionProvider.class );

    private final ServiceTracker tracker;

    private final BundleContext context;

    private final Map<ConnectionInformation, ConnectionManager> connections = new HashMap<ConnectionInformation, ConnectionManager> ();

    public ConnectionProvider ( final BundleContext context )
    {
        this.context = context;
        this.tracker = new ServiceTracker ( context, ConnectionRequest.class.getName (), this );
    }

    public synchronized void start ()
    {
        this.tracker.open ();
    }

    public synchronized void stop ()
    {
        this.tracker.close ();
    }

    public Object addingService ( final ServiceReference reference )
    {
        Object o = this.context.getService ( reference );
        try
        {
            final ConnectionRequest request = (ConnectionRequest)o;
            addRequest ( request );
            o = null;
            return request;
        }
        catch ( final Throwable e )
        {
            if ( o != null )
            {
                this.context.ungetService ( reference );
            }
        }
        return null;
    }

    public void modifiedService ( final ServiceReference reference, final Object service )
    {
        if ( service instanceof ConnectionRequest )
        {
            removeRequest ( (ConnectionRequest)service );
            addRequest ( (ConnectionRequest)service );
        }
    }

    public void removedService ( final ServiceReference reference, final Object service )
    {
        logger.debug ( "Removed service: {}", reference );

        if ( service instanceof ConnectionRequest )
        {
            removeRequest ( (ConnectionRequest)service );
        }
    }

    private synchronized void removeRequest ( final ConnectionRequest request )
    {
        logger.info ( "Request removed: {}", request );

        final ConnectionManager manager = this.connections.get ( request.getConnectionInformation () );
        if ( manager == null )
        {
            logger.warn ( "Unknown request: {}", request );
            return;
        }

        manager.removeRequest ( request );
        if ( manager.isIdle () )
        {
            logger.info ( "Dropping connection" );

            // if this was the last request .. remove it
            this.connections.remove ( request.getConnectionInformation () );
            manager.dispose ();
        }
    }

    private synchronized void addRequest ( final ConnectionRequest request )
    {
        logger.info ( "Found new request: {}", request );

        ConnectionManager manager = this.connections.get ( request.getConnectionInformation () );
        if ( manager == null )
        {
            logger.info ( "Create new connection: {}", request );
            manager = new ConnectionManager ( this.context, null, request.getConnectionInformation (), request.getAutoReconnectDelay (), request.isInitialOpen () );
            this.connections.put ( request.getConnectionInformation (), manager );
        }
        manager.addRequest ( request );
    }

}
