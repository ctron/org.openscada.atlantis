package org.openscada.core.ui.connection;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.core.connection.provider.ConnectionTracker.Listener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHolder
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionHolder.class );

    private final ConnectionInformation info;

    private final ConnectionDiscovererBean discoverer;

    private final BundleContext context;

    private final ConnectionTracker tracker;

    private final ConnectionRequest request;

    public ConnectionHolder ( final ConnectionDiscovererBean discoverer, final ConnectionInformation info ) throws InvalidSyntaxException
    {
        this.info = info;
        this.discoverer = discoverer;

        this.context = Activator.getDefault ().getBundle ().getBundleContext ();

        this.request = new ConnectionRequest ( null, info, 10000, true, false );

        this.tracker = new ConnectionTracker ( this.context, this.request, new Listener () {

            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                ConnectionHolder.this.setConnection ( connectionService );
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

    protected void setConnection ( final ConnectionService connectionService )
    {
        logger.info ( "Set connection: {}", connectionService );
    }

    public ConnectionDiscovererBean getDiscoverer ()
    {
        return this.discoverer;
    }

    @Override
    public String toString ()
    {
        return this.info.toString ();
    }

    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.info;
    }
}
