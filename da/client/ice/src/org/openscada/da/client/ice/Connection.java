/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client.ice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.ice.BrowserEntryHelper;
import org.openscada.utils.timing.Scheduler;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Util;
import OpenSCADA.Core.InvalidSessionException;
import OpenSCADA.Core.OperationNotSupportedException;
import OpenSCADA.DA.HivePrx;
import OpenSCADA.DA.HivePrxHelper;
import OpenSCADA.DA.InvalidItemException;
import OpenSCADA.DA.SessionPrx;
import OpenSCADA.DA.WriteAttributesResultEntry;
import OpenSCADA.DA.Browser.InvalidLocationException;

public class Connection implements org.openscada.da.client.Connection
{
    private static Logger _log = Logger.getLogger ( Connection.class );

    static
    {
        ConnectionFactory.registerDriverFactory ( new DriverFactory () );
    }

    private Scheduler _scheduler = new Scheduler ( true, "IceConnectionScheduler" );

    protected ConnectionState _state = ConnectionState.CLOSED;

    protected Communicator _communicator = null;

    private HivePrx _hive = null;

    private ConnectionInformation _connectionInformation = null;

    private String _args[] = null;

    private InitializationData _initData = null;

    private SessionPrx _session = null;

    private Map<String, ItemUpdateListener> _itemListenerMap = new HashMap<String, ItemUpdateListener> ();

    private Map<Location, FolderListener> _folderListenerMap = new HashMap<Location, FolderListener> ();

    private Set<ConnectionStateListener> _listeners = new HashSet<ConnectionStateListener> ();

    private ObjectAdapter _adapter;

    private DataCallbackImpl _dataCallback;

    private FolderCallbackImpl _folderCallback;

    private Queue<Runnable> _eventQueue = new LinkedList<Runnable> ();

    private Thread _eventPusher = null;

    private boolean _connectionRequested = false;

    public Connection ( ConnectionInformation connectionInformation )
    {
        super ();
        _connectionInformation = connectionInformation;

        _args = new String[0];
        if ( connectionInformation.getSubtargets () != null )
            _args = connectionInformation.getSubtargets ().toArray ( new String[0] );

        _initData = new InitializationData ();

        if ( _initData.properties == null )
        {
            _initData.properties = Util.createProperties ();
        }

        for ( Map.Entry<String, String> entry : connectionInformation.getProperties ().entrySet () )
        {
            _initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }

        _eventPusher = new Thread ( new Runnable () {

            public void run ()
            {
                pushEvents ();
            }
        } );
        _eventPusher.setDaemon ( true );
        _eventPusher.start ();

        // add the connection checker
        _scheduler.addJob ( new Runnable () {

            public void run ()
            {
                checkConnection ();
            }
        }, 5 * 1000 );
    }

    /**
     * Fetch the next event from the queue queue. If the event queue is empty
     * the call will block until a new event is placed in the queue.
     * @return The next event
     */
    protected Runnable pollEvent ()
    {
        synchronized ( _eventQueue )
        {
            if ( !_eventQueue.isEmpty () )
                return _eventQueue.poll ();

            while ( _eventQueue.isEmpty () )
            {
                try
                {
                    _eventQueue.wait ();
                }
                catch ( InterruptedException e )
                {
                }
            }

            return _eventQueue.poll ();
        }
    }

    protected void pushEvents ()
    {
        while ( true )
        {
            Runnable r = pollEvent ();

            // ignore errors in event handling
            try
            {
                r.run ();
            }
            catch ( Throwable e )
            {
            }
        }
    }

    /**
     * Schedule a re-connect.
     *
     */
    protected void scheduleReconnect ()
    {
        _scheduler.scheduleJob ( new Runnable () {

            public void run ()
            {
                performReconnect ();
            }
        }, 5 * 1000 );
    }

    protected void performReconnect ()
    {
        connect ();
    }

    public String getTarget ()
    {
        return _connectionInformation.getProperties ().get ( _connectionInformation.getTarget () );
    }

    public boolean isSecure ()
    {
        if ( !_connectionInformation.getProperties ().containsKey ( "secure" ) )
            return true;

        try
        {
            return Boolean.valueOf ( _connectionInformation.getProperties ().get ( "secure" ) );
        }
        catch ( Throwable t )
        {
            _log.warn ( "Unable to get property 'secure'. Defaulting to 'true'", t );
        }
        return true;
    }

    public boolean isAutoReconnect ()
    {
        return _connectionInformation.getProperties ().containsKey ( "auto-reconnect" );
    }

    public int getTimeout ()
    {
        if ( !_connectionInformation.getProperties ().containsKey ( "timeout" ) )
        {
            return -1;
        }

        try
        {
            return Integer.valueOf ( _connectionInformation.getProperties ().get ( "timeout" ) );
        }
        catch ( Throwable t )
        {
            _log.warn ( "Unable to get property 'timeout'. Defaulting to -1 (none)", t );
        }
        return -1;
    }

    protected HivePrx getHive () throws NoConnectionException
    {
        HivePrx hive;
        if ( ( hive = _hive ) != null )
        {
            return hive;
        }
        throw new NoConnectionException ();
    }

    protected Entry[] browse ( HivePrx hive, String[] path ) throws OperationException, NoConnectionException
    {
        try
        {
            return BrowserEntryHelper.fromIce ( hive.browse ( _session, path ) );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( OperationNotSupportedException e )
        {
            throw new org.openscada.core.OperationException ( e.message );
        }
        catch ( InvalidLocationException e )
        {
            throw new org.openscada.core.OperationException ( e );
        }
    }

    public Entry[] browse ( String[] path ) throws NoConnectionException, OperationException
    {
        return browse ( getHive (), path );
    }

    public Entry[] browse ( String[] path, int timeout ) throws NoConnectionException, OperationException
    {
        return browse ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), path );
    }

    public void browse ( String[] path, BrowseOperationCallback callback )
    {
        try
        {
            getHive ().browse_async ( new AsyncBrowseOperation ( callback ), _session, path );
        }
        catch ( NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    // write operation
    protected void write ( HivePrx hive, String itemName, Variant value ) throws OperationException
    {
        try
        {
            hive.write ( _session, itemName, VariantHelper.toIce ( value ) );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( new org.openscada.core.InvalidSessionException ().fillInStackTrace () );
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void write ( String item, Variant value ) throws OperationException, NoConnectionException
    {
        write ( getHive (), item, value );
    }

    public void write ( String item, Variant value, int timeout ) throws NoConnectionException, OperationException
    {
        write ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), item, value );
    }

    public void write ( String item, Variant value, WriteOperationCallback callback )
    {
        try
        {
            getHive ().write_async ( new AsyncWriteOperation ( callback ), _session, item, VariantHelper.toIce ( value ) );
        }
        catch ( NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    // write attributes operation

    public WriteAttributeResults writeAttributes ( String item, Map<String, Variant> attributes ) throws OperationException, NoConnectionException
    {
        return writeAttributes ( getHive (), item, attributes );
    }

    public WriteAttributeResults writeAttributes ( String item, Map<String, Variant> attributes, int timeout ) throws OperationException, NoConnectionException
    {
        return writeAttributes ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), item, attributes );
    }

    protected WriteAttributeResults writeAttributes ( HivePrx hive, String itemId, Map<String, Variant> attributes ) throws OperationException
    {
        try
        {
            WriteAttributesResultEntry[] result = hive.writeAttributes ( _session, itemId,
                    AttributesHelper.toIce ( attributes ) );
            return ConnectionHelper.fromIce ( result );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void writeAttributes ( String item, Map<String, Variant> attributes, WriteAttributeOperationCallback callback )
    {
        try
        {
            getHive ().writeAttributes_async ( new AsyncWriteAttributesOperation ( callback ), _session, item,
                    AttributesHelper.toIce ( attributes ) );
        }
        catch ( NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    public synchronized void addConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        _listeners.add ( connectionStateListener );
    }

    protected synchronized void setState ( ConnectionState state, Throwable error )
    {
        if ( !_state.equals ( state ) )
        {
            _state = state;
            notifyStateChange ( state, error );
        }
    }

    private synchronized void notifyStateChange ( ConnectionState state, Throwable error )
    {
        for ( ConnectionStateListener listener : _listeners.toArray ( new ConnectionStateListener[0] ) )
        {
            listener.stateChange ( this, state, error );
        }
    }

    public void checkConnection ()
    {
        SessionPrx session = _session;

        if ( session == null )
        {
            return;
        }

        try
        {
            session.ice_ping ();
        }
        catch ( Throwable e )
        {
            handleDisconnect ( e );
        }
    }

    public synchronized void connect ()
    {
        switch ( _state )
        {
        case CLOSED:
            break;
        default:
            return;
        }

        _connectionRequested = true;
        setState ( ConnectionState.CONNECTING, null );

        _communicator = Util.initialize ( _args, _initData );
        _adapter = _communicator.createObjectAdapter ( "Client" );
        _adapter.activate ();

        try
        {
            ObjectPrx prx = _communicator.stringToProxy ( getTarget () ).ice_secure ( isSecure () ).ice_timeout (
                    getTimeout () ).ice_twoway ();
            _hive = HivePrxHelper.checkedCast ( prx );

            setState ( ConnectionState.CONNECTED, null );

            _session = _hive.createSession ( _connectionInformation.getProperties () );

            // register data callback
            _dataCallback = new DataCallbackImpl ( this );
            Ice.Identity ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            _adapter.add ( _dataCallback, ident );
            _session.ice_getConnection ().setAdapter ( _adapter );
            _session.setDataCallback ( ident );

            // register folder callback
            _folderCallback = new FolderCallbackImpl ( this );
            ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            _adapter.add ( _folderCallback, ident );
            _session.ice_getConnection ().setAdapter ( _adapter );
            _session.setFolderCallback ( ident );

            setState ( ConnectionState.BOUND, null );
        }
        catch ( Exception e )
        {
            handleDisconnect ( e );
        }
    }

    /**
     * Schedule a reconnect if a connection is currently requested
     * @param e the error that caused the disconnect
     */
    protected synchronized void handleDisconnect ( Throwable e )
    {
        _log.info ( "handleDisconnect", e );

        _hive = null;
        _session = null;
        _adapter.deactivate ();
        _communicator.destroy ();

        _dataCallback = null;
        _folderCallback = null;

        _adapter = null;
        _communicator = null;

        setState ( ConnectionState.CLOSED, e );

        if ( isAutoReconnect () && _connectionRequested )
        {
            scheduleReconnect ();
        }
    }

    public synchronized void disconnect ()
    {
        switch ( _state )
        {
        case BOUND:
            break;
        default:
            return;
        }

        _log.debug ( "Shutting down connection" );
        _connectionRequested = false;
        try
        {
            _hive.closeSession ( _session );
        }
        catch ( Throwable e )
        {
            // don't care about this here
        }

        // now handle the disconnect
        handleDisconnect ( null );
    }

    public ConnectionState getState ()
    {
        return _state;
    }

    public synchronized void removeConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        _listeners.remove ( connectionStateListener );
    }

    public void subscriptionChange ( final String item, final SubscriptionState subscriptionState )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireSubscriptionChange ( item, subscriptionState );
                }
            } );
            _eventQueue.notify ();
        }
    }

    public void attributesChange ( final String itemId, final Map<String, Variant> attributes, final boolean full )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireAttributesChange ( itemId, attributes, full );
                }
            } );
            _eventQueue.notify ();
        }
    }

    public void valueChange ( final String itemId, final Variant variant, final boolean cache )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireValueChange ( itemId, variant, cache );
                }
            } );
            _eventQueue.notify ();
        }
    }

    public void folderChanged ( final Location location, final Entry[] entries, final String[] removed, final boolean full )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireFolderChange ( location, entries, removed, full );
                }
            } );
            _eventQueue.notify ();
        }
    }

    protected synchronized void fireAttributesChange ( String itemId, Map<String, Variant> attributes, boolean full )
    {
        ItemUpdateListener listener = _itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            listener.notifyAttributeChange ( attributes, full );
        }
    }

    protected synchronized void fireValueChange ( String itemId, Variant variant, boolean cache )
    {
        ItemUpdateListener listener = _itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            listener.notifyValueChange ( variant, cache );
        }
    }

    protected synchronized void fireSubscriptionChange ( String itemId, SubscriptionState subscriptionState )
    {
        ItemUpdateListener listener = _itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            listener.notifySubscriptionChange ( subscriptionState, null );
        }
    }

    protected void fireFolderChange ( Location location, Entry[] added, String[] removed, boolean full )
    {
        FolderListener listener;
        synchronized ( _folderListenerMap )
        {
            listener = _folderListenerMap.get ( location );
        }
        if ( listener != null )
        {
            listener.folderChanged ( Arrays.asList ( added ), Arrays.asList ( removed ), full );
        }
    }

    public synchronized ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener )
    {
        _log.debug ( String.format ( "Setting listener for item '%s' to %s", itemId, "" + listener ) );
        return _itemListenerMap.put ( itemId, listener );
    }

    public void subscribeItem ( String itemId ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().subscribeItem ( _session, itemId );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeItem ( String itemId ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().unsubscribeItem ( _session, itemId );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public FolderListener setFolderListener ( Location location, FolderListener listener )
    {
        synchronized ( _folderListenerMap )
        {
            return _folderListenerMap.put ( location, listener );
        }
    }

    public void subscribeFolder ( Location location ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().subscribeFolder ( _session, location.asArray () );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeFolder ( Location location ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().unsubscribeFolder ( _session, location.asArray () );
        }
        catch ( Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }

}
