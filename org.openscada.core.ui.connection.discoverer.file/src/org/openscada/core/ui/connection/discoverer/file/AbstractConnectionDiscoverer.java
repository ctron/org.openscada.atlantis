package org.openscada.core.ui.connection.discoverer.file;

import java.util.HashSet;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.ConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionDiscoveryListener;

public class AbstractConnectionDiscoverer implements ConnectionDiscoverer
{

    private final Set<ConnectionDiscoveryListener> listeners = new HashSet<ConnectionDiscoveryListener> ();

    protected Set<ConnectionInformation> connections = new HashSet<ConnectionInformation> ();

    public AbstractConnectionDiscoverer ()
    {
        super ();
    }

    protected synchronized void setConnections ( final Set<ConnectionInformation> result )
    {
        final Set<ConnectionInformation> added = new HashSet<ConnectionInformation> ( result );
        added.removeAll ( this.connections );

        final Set<ConnectionInformation> removed = new HashSet<ConnectionInformation> ( this.connections );
        removed.removeAll ( result );

        this.connections = result;

        fireDiscoveryUpdate ( added.toArray ( new ConnectionInformation[0] ), removed.toArray ( new ConnectionInformation[0] ) );
    }

    protected synchronized void fireDiscoveryUpdate ( final ConnectionInformation[] added, final ConnectionInformation[] removed )
    {
        for ( final ConnectionDiscoveryListener listener : this.listeners )
        {
            listener.discoveryUpdate ( added, removed );
        }
    }

    public synchronized void addConnectionListener ( final ConnectionDiscoveryListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            listener.discoveryUpdate ( this.connections.toArray ( new ConnectionInformation[0] ), null );
        }
    }

    public synchronized void removeConnectionListener ( final ConnectionDiscoveryListener listener )
    {
        this.listeners.remove ( listener );
    }

}