/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
import org.openscada.da.client.DataItemValue;
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

    private final Map<String, Map<String, DataItemValue>> lastDataItemValues = Collections.synchronizedMap ( new HashMap<String, Map<String, DataItemValue>> () );

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
        this.lastDataItemValues.put ( id, new HashMap<String, DataItemValue> () );
    }

    /**
     * @param connectionUri
     * @param id
     * @param prefix
     * @throws ClassNotFoundException
     */
    public void addConnection ( final String connectionUri, final String className, final String id, final String prefix ) throws ClassNotFoundException
    {
        Class.forName ( className );
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
        final SubConnection conOld = getCurrentConnection ();
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
            listener.connectionChanged ( idOld, conOld.getConnection (), id, getCurrentConnection ().getConnection () );
        }
        // return last values from current connection
        for ( final java.util.Map.Entry<String, ItemUpdateListener> entry : this.itemListeners.entrySet () )
        {
            final DataItemValue divNew = this.lastDataItemValues.get ( id ).get ( prepareItemId ( entry.getKey () ) );
            final DataItemValue divOld = this.lastDataItemValues.get ( idOld ).get ( prepareItemId ( entry.getKey (), conOld ) );
            // but only if value has really changed
            if ( dataItemValueEquals ( divOld, divNew ) )
            {
                entry.getValue ().notifyDataChange ( divNew.getValue (), divNew.getAttributes (), true );
            }
        }
    }

    /**
     * other than equals of DataItemValue it only compares value and attributes
     * @param dataItemValue1
     * @param dataItemValue2
     * @return
     */
    private boolean dataItemValueEquals ( final DataItemValue dataItemValue1, final DataItemValue dataItemValue2 )
    {
        if ( dataItemValue1 == dataItemValue2 )
        {
            return true;
        }
        if ( ( dataItemValue1 == null ) && ( dataItemValue2 != null ) )
        {
            return false;
        }
        if ( ( dataItemValue1 != null ) && ( dataItemValue2 == null ) )
        {
            return false;
        }
        if ( dataItemValue1.getValue () == null )
        {
            if ( dataItemValue2.getValue () != null )
            {
                return false;
            }
        }
        if ( !dataItemValue1.getValue ().equals ( dataItemValue2 ) )
        {
            return false;
        }
        else if ( dataItemValue1.getAttributes () == null )
        {
            if ( dataItemValue2.getAttributes () != null )
            {
                return false;
            }
        }
        else if ( !dataItemValue1.getAttributes ().equals ( dataItemValue2.getAttributes () ) )
        {
            return false;
        }
        return true;
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

    public Entry[] browse ( final String[] path ) throws NoConnectionException, OperationException
    {
        return browse ( new Location ( path ) );
    }

    public Entry[] browse ( final String[] path, final int timeout ) throws NoConnectionException, OperationException
    {
        return browse ( new Location ( path ), timeout );
    }

    public void browse ( final String[] path, final BrowseOperationCallback callback )
    {
        browse ( new Location ( path ), callback );
    }

    public Entry[] browse ( final Location location ) throws NoConnectionException, OperationException
    {
        return prepareEntries ( getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ) ) );
    }

    public Entry[] browse ( final Location location, final int timeout ) throws NoConnectionException, OperationException
    {
        return prepareEntries ( getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ), timeout ) );
    }

    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().browse ( prepareLocation ( location ), callback );
    }

    // Listener and subscriptions

    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        final FolderListener oldListener = this.folderListeners.put ( location, listener );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().setFolderListener ( prepareLocation ( location, connection ), new FolderListener () {
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

    public ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        final ItemUpdateListener oldListener = this.itemListeners.put ( itemId, listener );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().setItemUpdateListener ( prepareItemId ( itemId, connection ), new ItemUpdateListener () {
                public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
                {
                    // cache value
                    final DataItemValue div = new DataItemValue ( value, attributes, SubscriptionState.CONNECTED );
                    RedundantConnection.this.lastDataItemValues.get ( connection.getId () ).put ( prepareItemId ( itemId, connection ), div );
                    // propagate
                    if ( getCurrentConnection ().equals ( connection ) )
                    {
                        listener.notifyDataChange ( value, attributes, cache );
                    }
                }

                public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
                {
                    // cache value
                    DataItemValue div = RedundantConnection.this.lastDataItemValues.get ( connection.getId () ).get ( prepareItemId ( itemId, connection ) );
                    if ( div == null )
                    {
                        div = new DataItemValue ();
                    }
                    div.setSubscriptionState ( subscriptionState );
                    div.setSubscriptionError ( subscriptionError );
                    // propagate
                    if ( getCurrentConnection ().equals ( connection ) )
                    {
                        listener.notifySubscriptionChange ( subscriptionState, subscriptionError );
                    }
                }
            } );
        }
        return oldListener;
    }

    public void subscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.folderSubscriptions.add ( location );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().subscribeFolder ( prepareLocation ( location, connection ) );
        }
    }

    public void subscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        this.itemSubscriptions.add ( itemId );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().subscribeItem ( prepareItemId ( itemId, connection ) );
        }
    }

    public void unsubscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.folderSubscriptions.remove ( location );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().unsubscribeFolder ( prepareLocation ( location, connection ) );
        }
    }

    public void unsubscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        this.itemSubscriptions.add ( itemId );
        for ( final SubConnection connection : this.subConnections.values () )
        {
            connection.getConnection ().unsubscribeItem ( prepareItemId ( itemId, connection ) );
        }
    }

    // write operations, always acting on current connection

    public void write ( final String itemName, final Variant value ) throws NoConnectionException, OperationException
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value );
    }

    public void write ( final String itemName, final Variant value, final int timeout ) throws NoConnectionException, OperationException
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value, timeout );
    }

    public void write ( final String itemName, final Variant value, final WriteOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().write ( prepareItemId ( itemName ), value, callback );
    }

    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes ) throws NoConnectionException, OperationException
    {
        return getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes );
    }

    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes, final int timeout ) throws NoConnectionException, OperationException
    {
        return getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes, timeout );
    }

    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeOperationCallback callback )
    {
        getCurrentConnection ().getConnection ().writeAttributes ( prepareItemId ( itemId ), attributes, callback );
    }

    /**
     * adds connection state listener to current connection, will be switched 
     * to new connection when switched
     * @see org.openscada.core.client.Connection#addConnectionStateListener(org.openscada.core.client.ConnectionStateListener)
     */
    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connectionStateListeners.add ( connectionStateListener );
        getCurrentConnection ().getConnection ().addConnectionStateListener ( connectionStateListener );
    }

    /**
     * removes connection listener from current connection
     * @see org.openscada.core.client.Connection#removeConnectionStateListener(org.openscada.core.client.ConnectionStateListener)
     */
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
