package org.openscada.hd.ui.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.ui.IActionFilter;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.client.Connection;

public class ConnectionEntryBean extends AbstractPropertyChange implements ConnectionStateListener, IActionFilter, ItemListListener
{

    public static final String PROP_CONNECTION = "connection";

    private static final String PROP_CONNECTION_STATUS = "connectionStatus";

    private Connection connection;

    private final ConnectionInformation connectionInformation;

    private ConnectionState connectionStatus;

    private final WritableSet entries = new WritableSet ();

    private final Map<String, HistoricalItemEntryBean> itemCache = new HashMap<String, HistoricalItemEntryBean> ();

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
            this.connection = new org.openscada.hd.client.net.ConnectionImpl ( this.connectionInformation );
            this.connection.addConnectionStateListener ( this );
            this.connection.addListListener ( this );
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
        final ConnectionState oldState = this.connectionStatus;
        this.connectionStatus = state;
        firePropertyChange ( PROP_CONNECTION_STATUS, oldState, state );
    }

    public boolean testAttribute ( final Object target, final String name, final String value )
    {
        if ( "hasConnection".equals ( name ) )
        {
            return this.connection != null == Boolean.parseBoolean ( value );
        }
        return false;
    }

    public ObservableSet getEntries ()
    {
        return this.entries;
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        this.entries.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                if ( full )
                {
                    ConnectionEntryBean.this.entries.clear ();
                }

                if ( removed != null )
                {
                    for ( final String item : removed )
                    {
                        ConnectionEntryBean.this.itemCache.remove ( item );
                    }
                }
                if ( addedOrModified != null )
                {
                    for ( final HistoricalItemInformation item : addedOrModified )
                    {
                        final HistoricalItemEntryBean entry = new HistoricalItemEntryBean ( ConnectionEntryBean.this, item );
                        ConnectionEntryBean.this.itemCache.put ( item.getId (), entry );
                        ConnectionEntryBean.this.entries.add ( entry );
                    }
                }

            }
        } );

    }
}
