package org.openscada.da.client.connection.manager.view;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.client.Connection;

public class ConnectionEntry extends AbstractModelObject implements ConnectionStateListener
{
    private static final String PROP_CONNECTION_STATE = "connectionState"; //$NON-NLS-1$

    protected Connection connection;

    protected ConnectionState connectionState;

    public ConnectionEntry ( final Connection connection )
    {
        this.connection = connection;
        synchronized ( this )
        {
            this.connection.addConnectionStateListener ( this );
            stateChange ( connection, this.connection.getState (), null );
        }
    }

    public void dispose ()
    {
        this.connection.removeConnectionStateListener ( this );
        this.connection = null;
    }

    public synchronized void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        final ConnectionState oldState = this.connectionState;
        this.connectionState = state;

        firePropertyChange ( PROP_CONNECTION_STATE, oldState, state );
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connection.getConnectionInformation ();
    }

    public ConnectionState getConnectionState ()
    {
        return this.connectionState;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }
}
