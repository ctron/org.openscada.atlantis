package org.openscada.core.ui.connection.data;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IAdaptable;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.creator.ConnectionCreatorHelper;
import org.openscada.utils.beans.AbstractPropertyChange;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
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

    private volatile ConnectionService connectionService;

    private volatile ConnectionState connectionState;

    private Throwable connectionError;

    private final BundleContext context;

    private ServiceRegistration serviceRegistration;

    public ConnectionHolder ( final ConnectionDiscovererBean discoverer, final ConnectionInformation info ) throws InvalidSyntaxException
    {
        this.info = info;
        this.discoverer = discoverer;

        this.context = Activator.getDefault ().getBundle ().getBundleContext ();

        createConnection ();
    }

    private synchronized void createConnection ()
    {
        final ConnectionService connectionService = ConnectionCreatorHelper.createConnection ( this.info, null );
        if ( connectionService != null )
        {
            connectionService.getConnection ().addConnectionStateListener ( this );
            setConnectionService ( connectionService );
            setConnectionState ( ConnectionState.CLOSED );
            setConnectionError ( null );

            registerConnection ();
        }
    }

    /**
     * Register the current connection as an OSGi service
     */
    private void registerConnection ()
    {
        final Class<?>[] interfaces = this.connectionService.getSupportedInterfaces ();

        final String[] clazzes = new String[interfaces.length];

        int i = 0;
        for ( final Class<?> iface : interfaces )
        {
            clazzes[i] = iface.getName ();
            i++;
        }

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( ConnectionService.CONNECTION_URI, this.info.toString () );
        this.serviceRegistration = this.context.registerService ( clazzes, this.connectionService, properties );
    }

    private synchronized void destroyConnection ()
    {
        if ( this.serviceRegistration != null )
        {
            this.serviceRegistration.unregister ();
            this.serviceRegistration = null;
        }
        if ( this.connectionService != null )
        {
            this.connectionService.getConnection ().removeConnectionStateListener ( this );
            this.connectionService.disconnect ();
            this.connectionService = null;
            setConnectionService ( null );
            setConnectionState ( null );
            setConnectionError ( null );
        }
    }

    public synchronized void connect ()
    {
        if ( this.connectionService != null )
        {
            this.connectionService.connect ();
        }
    }

    public synchronized void disconnect ()
    {
        if ( this.connectionService != null )
        {
            this.connectionService.disconnect ();
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
        destroyConnection ();
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

    protected void setConnectionService ( final ConnectionService connectionService )
    {
        final ConnectionService olcConnectionService = connectionService;
        this.connectionService = connectionService;
        firePropertyChange ( PROP_CONNECTION_SERVICE, olcConnectionService, connectionService );
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
