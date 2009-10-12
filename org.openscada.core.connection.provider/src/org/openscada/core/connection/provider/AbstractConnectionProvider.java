package org.openscada.core.connection.provider;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionProvider
{
    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionProvider.class );

    protected abstract AbstractConnectionManager createConnectionManager ( final ConnectionRequest request );

    protected final ServiceTracker tracker;

    protected final BundleContext context;

    private final Map<ConnectionInformation, AbstractConnectionManager> connections = new HashMap<ConnectionInformation, AbstractConnectionManager> ();

    public AbstractConnectionProvider ( final BundleContext context )
    {
        super ();
        this.context = context;
        this.tracker = new ServiceTracker ( context, ConnectionRequest.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                AbstractConnectionProvider.this.removedService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                AbstractConnectionProvider.this.modifiedService ( reference, service );
            }

            public Object addingService ( final ServiceReference reference )
            {
                return AbstractConnectionProvider.this.addingService ( reference );
            }
        } );
    }

    public synchronized void start ()
    {
        this.tracker.open ();
    }

    public synchronized void stop ()
    {
        this.tracker.close ();
    }

    protected Object addingService ( final ServiceReference reference )
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

    protected void modifiedService ( final ServiceReference reference, final Object service )
    {
        if ( service instanceof ConnectionRequest )
        {
            removeRequest ( (ConnectionRequest)service );
            addRequest ( (ConnectionRequest)service );
        }
    }

    protected void removedService ( final ServiceReference reference, final Object service )
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

        final AbstractConnectionManager manager = this.connections.get ( request.getConnectionInformation () );
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

        AbstractConnectionManager manager = this.connections.get ( request.getConnectionInformation () );
        if ( manager == null )
        {
            logger.info ( "Create new connection: {}", request );
            manager = createConnectionManager ( request );
            this.connections.put ( request.getConnectionInformation (), manager );
        }
        manager.addRequest ( request );
    }

}