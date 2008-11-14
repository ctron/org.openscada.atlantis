package org.openscada.da.server.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;

/**
 * provides a connection which provides the possibility to switch
 * between given connections
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public class RedundantConnection implements Connection
{
    private String separator = ".";

    private String exposeAs = "Redundant";

    private final Map<String, SubConnection> subConnections = new HashMap<String, SubConnection> ();

    private String currentConnectionId;

    private final List<ConnectionStateListener> connectionStateListeners = Collections.synchronizedList ( new ArrayList<ConnectionStateListener> () );

    private final Map<String, ItemUpdateListener> itemListeners = Collections.synchronizedMap ( new HashMap<String, ItemUpdateListener> () );

    private final Map<Location, FolderListener> folderListeners = Collections.synchronizedMap ( new HashMap<Location, FolderListener> () );

    private final List<String> itemSubscriptions = Collections.synchronizedList ( new ArrayList<String> () );

    private final List<Location> folderSubscriptions = Collections.synchronizedList ( new ArrayList<Location> () );

    private final List<RedundantConnectionChangedListener> redundantConnectionChangedListeners = Collections.synchronizedList ( new ArrayList<RedundantConnectionChangedListener> () );

    /**
     * @param separator
     * @param exposeAs
     */
    public RedundantConnection ( final String separator, final String exposeAs )
    {
        this.separator = separator;
        this.exposeAs = exposeAs;
    }

    /**
     * @param connection
     * @param id
     * @param prefix
     */
    public void addConnection ( final Connection connection, final String id, final String prefix )
    {
        this.subConnections.put ( id, new SubConnection ( connection, id, prefix ) );
        if ( this.subConnections.size () == 1 )
        {
            this.currentConnectionId = id;
        }
    }

    /**
     * @param connectionUri
     * @param id
     * @param prefix
     * @throws ClassNotFoundException
     */
    public void addConnection ( final String connectionUri, final String id, final String prefix ) throws ClassNotFoundException
    {
        Class.forName ( "org.openscada.da.client.net.Connection" );
        final Connection connection = (Connection)ConnectionFactory.create ( ConnectionInformation.fromURI ( connectionUri ) );
        addConnection ( connection, id, prefix );
    }

    /**
     * @param listener
     */
    public void addConnectionChangedListener ( final RedundantConnectionChangedListener listener )
    {
        this.redundantConnectionChangedListeners.add ( listener );
    }

    /**
     * @param listener
     */
    public void removeConnectionChangedListener ( final RedundantConnectionChangedListener listener )
    {
        this.redundantConnectionChangedListeners.remove ( listener );
    }

    /**
     * @param id
     * @throws NoConnectionException
     * @throws OperationException
     */
    public void switchConnection ( final String id ) throws NoConnectionException, OperationException
    {
        final String idOld = this.currentConnectionId;
        final Connection conOld = getCurrentConnection ().getConnection ();
        for ( final ConnectionStateListener listener : this.connectionStateListeners )
        {
            getCurrentConnection ().getConnection ().removeConnectionStateListener ( listener );
        }
        this.currentConnectionId = id;
        for ( final ConnectionStateListener listener : this.connectionStateListeners )
        {
            getCurrentConnection ().getConnection ().addConnectionStateListener ( listener );
        }
        for ( final RedundantConnectionChangedListener listener : this.redundantConnectionChangedListeners )
        {
            listener.connectionChanged ( idOld, conOld, id, getCurrentConnection ().getConnection () );
        }
    }

    /**
     * @return prefix
     */
    public String getExposeAs ()
    {
        return this.exposeAs;
    }

    /**
     * @return string which separates prefix from rest of item (normally .)
     */
    public String getSeparator ()
    {
        return this.separator;
    }

    /**
     * @return current connection
     */
    public SubConnection getCurrentConnection ()
    {
        return this.subConnections.get ( this.currentConnectionId );
    }

    /**
     * @return the list of connections between which may be switched 
     */
    public Map<String, SubConnection> getUnderlyingConnections ()
    {
        return Collections.unmodifiableMap ( this.subConnections );
    }

    /**
     * checks if a connection with this id exists at all
     * @param id which should be checked
     * @return
     */
    public boolean isValidConnection ( final String id )
    {
        return this.subConnections.containsKey ( id );
    }

    // actual Connection Implementation

    @Override
    public Entry[] browse ( final String[] path ) throws NoConnectionException, OperationException
    {
        return browse ( new Location ( path ) );
    }

    @Override
    public Entry[] browse ( final String[] path, final int timeout ) throws NoConnectionException, OperationException
    {
        return browse ( new Location ( path ), timeout );
    }

    @Override
    public void browse ( final String[] path, final BrowseOperationCallback callback )
    {
        browse ( new Location ( path ), callback );
    }

    @Override
    public Entry[] browse ( final Location location ) throws NoConnectionException, OperationException
    {
        return prepareEntries ( getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ) ) );
    }

    @Override
    public Entry[] browse ( final Location location, final int timeout ) throws NoConnectionException, OperationException
    {
        return prepareEntries ( getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ), timeout ) );
    }

    @Override
    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ), callback );
    }

    // Listener and subscriptions

    @Override
    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        final FolderListener oldListener = this.folderListeners.put ( location, listener );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().setFolderListener ( prepareLocation ( location, connection ), new FolderListener () {
                @Override
                public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
                {
                    if ( getCurrentConnection ().equals ( connection ) )
                    {
                        listener.folderChanged ( added, removed, full );
                    }
                }
            } );
        }
        return oldListener;
    }

    @Override
    public ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        final ItemUpdateListener oldListener = this.itemListeners.put ( itemId, listener );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().setItemUpdateListener ( prepareItemId ( itemId, connection ), new ItemUpdateListener () {
                @Override
                public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
                {
                    if ( getCurrentConnection ().equals ( connection ) )
                    {
                        listener.notifyDataChange ( value, attributes, cache );
                    }
                }

                @Override
                public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
                {
                    if ( getCurrentConnection ().equals ( connection ) )
                    {
                        listener.notifySubscriptionChange ( subscriptionState, subscriptionError );
                    }
                }
            } );
        }
        return oldListener;
    }

    @Override
    public void subscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.folderSubscriptions.add ( location );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().subscribeFolder ( prepareLocation ( location, connection ) );
        }
    }

    @Override
    public void subscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        this.itemSubscriptions.add ( itemId );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().subscribeItem ( prepareItemId ( itemId, connection ) );
        }
    }

    @Override
    public void unsubscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.folderSubscriptions.remove ( location );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().unsubscribeFolder ( prepareLocation ( location, connection ) );
        }
    }

    @Override
    public void unsubscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        this.itemSubscriptions.add ( itemId );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().unsubscribeItem ( prepareItemId ( itemId, connection ) );
        }
    }

    // write operations, always acting on current connection

    @Override
    public void write ( final String itemName, final Variant value ) throws NoConnectionException, OperationException
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value );
    }

    @Override
    public void write ( final String itemName, final Variant value, final int timeout ) throws NoConnectionException, OperationException
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value, timeout );
    }

    @Override
    public void write ( final String itemName, final Variant value, final WriteOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value, callback );
    }

    @Override
    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes ) throws NoConnectionException, OperationException
    {
        return getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes );
    }

    @Override
    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes, final int timeout ) throws NoConnectionException, OperationException
    {
        return getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes, timeout );
    }

    @Override
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes, callback );
    }

    /**
     * adds connection state listener to current connection, will be switched 
     * to new connection when switched
     * @see org.openscada.core.client.Connection#addConnectionStateListener(org.openscada.core.client.ConnectionStateListener)
     */
    @Override
    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connectionStateListeners.add ( connectionStateListener );
        getCurrentConnection ().getConnection ().addConnectionStateListener ( connectionStateListener );
    }

    /**
     * removes connection listener from current connection
     * @see org.openscada.core.client.Connection#removeConnectionStateListener(org.openscada.core.client.ConnectionStateListener)
     */
    @Override
    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connectionStateListeners.remove ( connectionStateListener );
        getCurrentConnection ().getConnection ().removeConnectionStateListener ( connectionStateListener );
    }

    /**
     * connect all underlying connections, but connection state listeners are 
     * only listening on active connection 
     * @see org.openscada.core.client.Connection#connect()
     */
    @Override
    public void connect ()
    {
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().connect ();
        }
    }

    /**
     * disconnect all underlying connections, but connection state listeners are 
     * only listening on active connection 
     * @see org.openscada.core.client.Connection#disconnect()
     */
    @Override
    public void disconnect ()
    {
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().disconnect ();
        }
    }

    /**
     * returns state of current connection
     * @see org.openscada.core.client.Connection#getState()
     */
    @Override
    public ConnectionState getState ()
    {
        return getCurrentConnection ().getConnection ().getState ();
    }

    // utility methods

    protected Entry[] prepareEntries ( final Entry[] entries, final SubConnection subConnection )
    {
        return entries;
    }

    protected Entry[] prepareEntries ( final Entry[] entries )
    {
        return prepareEntries ( entries, getCurrentConnection () );
    }

    protected String prepareItemId ( final String itemId, final SubConnection subConnection )
    {
        return itemId.replaceFirst ( Pattern.quote ( this.exposeAs + this.separator ), subConnection.getPrefix () + this.separator );
    }

    protected String prepareItemId ( final String itemId )
    {
        return prepareItemId ( itemId, getCurrentConnection () );
    }

    protected Location prepareLocation ( final Location location, final SubConnection subConnection )
    {
        return location;
    }

    protected Location prepareLocation ( final Location location )
    {
        return prepareLocation ( location, getCurrentConnection () );
    }
}
