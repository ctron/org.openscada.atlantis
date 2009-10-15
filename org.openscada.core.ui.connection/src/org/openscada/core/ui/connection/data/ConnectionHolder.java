package org.openscada.core.ui.connection.data;

import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.core.connection.provider.ConnectionTracker.Listener;
import org.openscada.core.ui.connection.Activator;
import org.openscada.utils.beans.AbstractPropertyChange;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHolder extends AbstractPropertyChange implements ConnectionStateListener, IAdaptable
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionHolder.class );

    public static final String PROP_CONNECTION_SERVICE = "connectionService";

    public static final String PROP_CONNECTION_STATE = "connectionState";

    public static final String PROP_CONNECTION_ERROR = "connectionError";

    private final ConnectionInformation info;

    private final ConnectionDiscovererBean discoverer;

    private final BundleContext context;

    private final ConnectionTracker tracker;

    private final ConnectionRequest request;

    private ConnectionService connectionService;

    private volatile ConnectionState connectionState;

    private Throwable connectionError;

    public ConnectionHolder ( final ConnectionDiscovererBean discoverer, final ConnectionInformation info ) throws InvalidSyntaxException
    {
        this.info = info;
        this.discoverer = discoverer;

        this.context = Activator.getDefault ().getBundle ().getBundleContext ();

        this.request = new ConnectionRequest ( UUID.randomUUID ().toString (), info, null, true, false );

        this.tracker = new ConnectionTracker ( this.context, this.request, new Listener () {

            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                ConnectionHolder.this.setConnectionService ( connectionService );
            }
        } );
        this.tracker.listen ();
    }

    public synchronized void connect ()
    {
        if ( this.connectionService != null )
        {
            this.connectionService.connect ();
        }
        else
        {
            this.tracker.request ();
        }
    }

    public synchronized void disconnect ()
    {
        this.tracker.unrequest ();
    }

    protected void setConnectionService ( final ConnectionService connectionService )
    {
        logger.info ( "Set connection: {}", connectionService );

        final ConnectionService oldConnectionService = this.connectionService;
        this.connectionService = connectionService;
        firePropertyChange ( PROP_CONNECTION_SERVICE, oldConnectionService, connectionService );

        if ( oldConnectionService != null )
        {
            oldConnectionService.getConnection ().removeConnectionStateListener ( this );
        }

        if ( connectionService != null )
        {
            setConnectionState ( connectionService.getConnection ().getState () );
            setConnectionError ( null );
            connectionService.getConnection ().addConnectionStateListener ( this );
        }
        else
        {
            setConnectionError ( null );
            setConnectionState ( null );
        }
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

    public ConnectionService getConnectionService ()
    {
        return this.connectionService;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.info;
    }

    public ConnectionState getConnectionState ()
    {
        return this.connectionState;
    }

    protected void setConnectionState ( final ConnectionState connectionState )
    {
        final ConnectionState oldConnectionState = this.connectionState;
        this.connectionState = connectionState;
        firePropertyChange ( PROP_CONNECTION_STATE, oldConnectionState, connectionState );
    }

    public Throwable getConnectionError ()
    {
        return this.connectionError;
    }

    protected void setConnectionError ( final Throwable connectionError )
    {
        final Throwable olcConnectionError = connectionError;
        this.connectionError = connectionError;
        firePropertyChange ( PROP_CONNECTION_ERROR, olcConnectionError, connectionError );

    }

    public void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        logger.debug ( "Connection state changed: {}", state );
        setConnectionState ( state );
        setConnectionError ( error );
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        if ( adapter == ConnectionService.class )
        {
            return this.connectionService;
        }
        return null;
    }
}
