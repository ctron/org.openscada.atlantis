package org.openscada.core.ui.connection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.core.ConnectionInformation;

public abstract class AbstractConnectionDiscoverer implements ConnectionDiscoverer
{

    private final Set<ConnectionDiscoveryListener> listeners = new HashSet<ConnectionDiscoveryListener> ();

    private Set<ConnectionInformation> connections = new HashSet<ConnectionInformation> ();

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

    /**
     * Add and announce a new connection
     * <p>
     * The connection can also be <code>null</code> in which the method will return <code>false</code>
     * </p>
     * <p>
     * If the connection was already known, <code>false</code> will be returned and no event will be emitted
     * </p>
     * @param connectionInformation a new connection
     * @return <code>true</code> if the new connection was added
     */
    public synchronized boolean addConnection ( final ConnectionInformation connectionInformation )
    {
        if ( connectionInformation == null )
        {
            return false;
        }

        if ( this.connections.add ( connectionInformation ) )
        {
            fireDiscoveryUpdate ( new ConnectionInformation[] { connectionInformation }, null );
            return true;
        }
        return false;
    }

    /**
     * Remove a connection
     * <p>
     * The connection can also be <code>null</code> in which the method will return <code>false</code>
     * </p>
     * <p>
     * If the connection was not known, <code>false</code> will be returned and no event will be emitted
     * </p>
     * @param connectionInformation the connection to remove
     * @return <code>true</code> if the connection was removed
     */
    public synchronized boolean removeConnection ( final ConnectionInformation connectionInformation )
    {
        if ( connectionInformation == null )
        {
            return false;
        }

        if ( this.connections.remove ( connectionInformation ) )
        {
            fireDiscoveryUpdate ( null, new ConnectionInformation[] { connectionInformation } );
            return true;
        }
        return false;
    }

    public Set<ConnectionInformation> getConnections ()
    {
        return Collections.unmodifiableSet ( this.connections );
    }

    public synchronized void dispose ()
    {
        fireDiscoveryUpdate ( null, this.connections.toArray ( new ConnectionInformation[0] ) );
        this.listeners.clear ();
        this.connections.clear ();
    }

}