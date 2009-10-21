package org.openscada.core.connection.provider;

import java.util.Map;

import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConnectionTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionTracker.class );

    private SingleServiceTracker tracker;

    private Filter filter;

    private final Listener listener;

    private ConnectionService service;

    private final BundleContext context;

    public interface Listener
    {
        public void setConnection ( final ConnectionService connectionService );
    }

    public ConnectionTracker ( final BundleContext context, final Listener listener )
    {
        this.context = context;
        this.listener = listener;
    }

    protected SingleServiceTracker createTracker ()
    {
        synchronized ( this )
        {
            if ( this.filter == null )
            {
                this.filter = createFilter ();
            }

            if ( this.filter != null )
            {
                return new SingleServiceTracker ( this.context, this.filter, new SingleServiceListener () {

                    public void serviceChange ( final ServiceReference reference, final Object service )
                    {
                        ConnectionTracker.this.setService ( reference, (ConnectionService)service );
                    }
                } );
            }
            return null;
        }
    }

    protected Filter createFilter ()
    {
        try
        {
            return FilterUtil.createAndFilter ( ConnectionService.class.getName (), createFilterParameters () );
        }
        catch ( final InvalidSyntaxException e )
        {
            logger.warn ( "Failed to create filter", e );
            return null;
        }
    }

    protected abstract Map<String, String> createFilterParameters ();

    public synchronized void listen ()
    {
        if ( this.tracker == null )
        {
            this.tracker = createTracker ();
        }

        if ( this.tracker != null )
        {
            logger.debug ( "Opening tracker" );
            this.tracker.open ();
        }
    }

    public synchronized void open ()
    {
        listen ();
    }

    public synchronized void close ()
    {
        unlisten ();
    }

    public synchronized void unlisten ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
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

    public boolean waitForService ( final long timeout ) throws InterruptedException
    {
        return this.tracker.waitForService ( timeout ) != null;
    }
}
