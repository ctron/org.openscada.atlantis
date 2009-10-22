package org.openscada.ae.ui.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.ui.IActionFilter;
import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.client.Connection;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class ConnectionEntryBean extends AbstractPropertyChange implements ConnectionStateListener, IActionFilter, BrowserListener
{

    public static final String PROP_CONNECTION = "connection";

    private static final String PROP_CONNECTION_STATUS = "connectionStatus";

    private Connection connection;

    private final ConnectionInformation connectionInformation;

    private ConnectionState connectionStatus;

    private final WritableSet entries = new WritableSet ();

    private final Map<String, BrowserEntryBean> browserCache = new HashMap<String, BrowserEntryBean> ();

    public ConnectionEntryBean ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public void connect ()
    {
        if ( this.connection == null )
        {
            this.connection = new org.openscada.ae.client.net.ConnectionImpl ( this.connectionInformation, true );
            this.connection.addConnectionStateListener ( this );
            this.connection.addBrowserListener ( this );
            firePropertyChange ( PROP_CONNECTION, null, this.connection );
        }
        this.connection.connect ();
    }

    public void disconnect ()
    {
        if ( this.connection != null )
        {
            this.connection.disconnect ();
        }
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public ConnectionState getConnectionStatus ()
    {
        return this.connectionStatus;
    }

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        setConnectionStatus ( state );
    }

    private void setConnectionStatus ( final ConnectionState state )
    {
        ConnectionState oldState = this.connectionStatus;
        this.connectionStatus = state;
        firePropertyChange ( PROP_CONNECTION_STATUS, oldState, state );
    }

    public boolean testAttribute ( final Object target, final String name, final String value )
    {
        if ( "hasConnection".equals ( name ) )
        {
            return ( this.connection != null ) == Boolean.parseBoolean ( value );
        }
        return false;
    }

    public void dataChanged ( final BrowserEntry[] added, final String[] removed )
    {
        final Collection<BrowserEntryBean> removedEntries = new LinkedList<BrowserEntryBean> ();
        final Collection<BrowserEntryBean> addedEntries = new LinkedList<BrowserEntryBean> ();

        synchronized ( this.browserCache )
        {
            if ( removed != null )
            {
                for ( String id : removed )
                {
                    BrowserEntryBean entry = this.browserCache.remove ( id );
                    if ( entry != null )
                    {
                        removedEntries.add ( entry );
                    }
                }
            }
            if ( added != null )
            {
                for ( BrowserEntry entry : added )
                {
                    BrowserEntryBean bean = new BrowserEntryBean ( this, entry );
                    this.browserCache.put ( entry.getId (), bean );
                    addedEntries.add ( bean );
                }
            }
        }

        this.entries.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                ConnectionEntryBean.this.entries.removeAll ( removedEntries );
                ConnectionEntryBean.this.entries.addAll ( addedEntries );
            }
        } );

    }

    public ObservableSet getEntries ()
    {
        return this.entries;
    }
}
