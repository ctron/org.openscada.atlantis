/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.client.ice;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import org.openscada.utils.concurrent.NamedThreadFactory;

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
    private static Logger log = Logger.getLogger ( Connection.class );

    static
    {
        ConnectionFactory.registerDriverFactory ( new DriverFactory () );
    }

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "IceConnectionScheduler" ) );

    protected ConnectionState state = ConnectionState.CLOSED;

    protected Communicator communicator = null;

    private HivePrx hive = null;

    private ConnectionInformation connectionInformation = null;

    private String args[] = null;

    private InitializationData initData = null;

    private SessionPrx session = null;

    private final Map<String, ItemUpdateListener> itemListenerMap = new ConcurrentHashMap<String, ItemUpdateListener> ();

    private final Map<Location, FolderListener> folderListenerMap = new ConcurrentHashMap<Location, FolderListener> ();

    private final Set<ConnectionStateListener> listeners = new HashSet<ConnectionStateListener> ();

    private ObjectAdapter adapter;

    private DataCallbackImpl dataCallback;

    private FolderCallbackImpl folderCallback;

    private final Queue<Runnable> eventQueue = new LinkedList<Runnable> ();

    private Thread eventPusher = null;

    private boolean connectionRequested = false;

    private Executor executor = new Executor () {

        public void execute ( final Runnable command )
        {
            try
            {
                command.run ();
            }
            catch ( final Throwable e )
            {
                log.info ( "Uncaught exception in connection executor", e );
            }
        }
    };

    public Connection ( final ConnectionInformation connectionInformation )
    {
        super ();
        this.connectionInformation = connectionInformation;

        this.args = new String[0];
        if ( connectionInformation.getSubtargets () != null )
        {
            this.args = connectionInformation.getSubtargets ().toArray ( new String[0] );
        }

        this.initData = new InitializationData ();

        if ( this.initData.properties == null )
        {
            this.initData.properties = Util.createProperties ();
        }

        for ( final Map.Entry<String, String> entry : connectionInformation.getProperties ().entrySet () )
        {
            this.initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }

        this.eventPusher = new Thread ( new Runnable () {

            public void run ()
            {
                pushEvents ();
            }
        } );
        this.eventPusher.setDaemon ( true );
        this.eventPusher.start ();

        // add the connection checker
        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                checkConnection ();
            }
        }, 5 * 1000, 5 * 1000, TimeUnit.MILLISECONDS );
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public Map<String, String> getSessionProperties ()
    {
        return Collections.emptyMap ();
    }

    /**
     * Fetch the next event from the queue queue. If the event queue is empty
     * the call will block until a new event is placed in the queue.
     * @return The next event
     */
    protected Runnable pollEvent ()
    {
        synchronized ( this.eventQueue )
        {
            if ( !this.eventQueue.isEmpty () )
            {
                return this.eventQueue.poll ();
            }

            while ( this.eventQueue.isEmpty () )
            {
                try
                {
                    this.eventQueue.wait ();
                }
                catch ( final InterruptedException e )
                {
                }
            }

            return this.eventQueue.poll ();
        }
    }

    protected void pushEvents ()
    {
        while ( true )
        {
            final Runnable r = pollEvent ();

            // ignore errors in event handling
            try
            {
                r.run ();
            }
            catch ( final Throwable e )
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
        this.scheduler.schedule ( new Runnable () {

            public void run ()
            {
                performReconnect ();
            }
        }, 5 * 1000, TimeUnit.MILLISECONDS );
    }

    protected void performReconnect ()
    {
        connect ();
    }

    public String getTarget ()
    {
        return this.connectionInformation.getProperties ().get ( this.connectionInformation.getTarget () );
    }

    public boolean isSecure ()
    {
        if ( !this.connectionInformation.getProperties ().containsKey ( "secure" ) )
        {
            return true;
        }

        try
        {
            return Boolean.valueOf ( this.connectionInformation.getProperties ().get ( "secure" ) );
        }
        catch ( final Throwable t )
        {
            log.warn ( "Unable to get property 'secure'. Defaulting to 'true'", t );
        }
        return true;
    }

    public boolean isAutoReconnect ()
    {
        return this.connectionInformation.getProperties ().containsKey ( "auto-reconnect" );
    }

    public int getTimeout ()
    {
        if ( !this.connectionInformation.getProperties ().containsKey ( "timeout" ) )
        {
            return -1;
        }

        try
        {
            return Integer.valueOf ( this.connectionInformation.getProperties ().get ( "timeout" ) );
        }
        catch ( final Throwable t )
        {
            log.warn ( "Unable to get property 'timeout'. Defaulting to -1 (none)", t );
        }
        return -1;
    }

    protected HivePrx getHive () throws NoConnectionException
    {
        HivePrx hive;
        if ( ( hive = this.hive ) != null )
        {
            return hive;
        }
        throw new NoConnectionException ();
    }

    protected Entry[] browse ( final HivePrx hive, final String[] path ) throws OperationException, NoConnectionException
    {
        try
        {
            return BrowserEntryHelper.fromIce ( hive.browse ( this.session, path ) );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( final OperationNotSupportedException e )
        {
            throw new org.openscada.core.OperationException ( e.message );
        }
        catch ( final InvalidLocationException e )
        {
            throw new org.openscada.core.OperationException ( e );
        }
    }

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

    // write operation
    protected void write ( final HivePrx hive, final String itemName, final Variant value ) throws OperationException
    {
        try
        {
            hive.write ( this.session, itemName, VariantHelper.toIce ( value ) );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( new org.openscada.core.InvalidSessionException ().fillInStackTrace () );
        }
        catch ( final InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void write ( final String item, final Variant value ) throws OperationException, NoConnectionException
    {
        write ( getHive (), item, value );
    }

    public void write ( final String item, final Variant value, final int timeout ) throws NoConnectionException, OperationException
    {
        write ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), item, value );
    }

    public void write ( final String item, final Variant value, final WriteOperationCallback callback )
    {
        try
        {
            getHive ().write_async ( new AsyncWriteOperation ( callback ), this.session, item, VariantHelper.toIce ( value ) );
        }
        catch ( final NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    // write attributes operation

    public WriteAttributeResults writeAttributes ( final String item, final Map<String, Variant> attributes ) throws OperationException, NoConnectionException
    {
        return writeAttributes ( getHive (), item, attributes );
    }

    public WriteAttributeResults writeAttributes ( final String item, final Map<String, Variant> attributes, final int timeout ) throws OperationException, NoConnectionException
    {
        return writeAttributes ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), item, attributes );
    }

    protected WriteAttributeResults writeAttributes ( final HivePrx hive, final String itemId, final Map<String, Variant> attributes ) throws OperationException
    {
        try
        {
            final WriteAttributesResultEntry[] result = hive.writeAttributes ( this.session, itemId, AttributesHelper.toIce ( attributes ) );
            return ConnectionHelper.fromIce ( result );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void writeAttributes ( final String item, final Map<String, Variant> attributes, final WriteAttributeOperationCallback callback )
    {
        try
        {
            getHive ().writeAttributes_async ( new AsyncWriteAttributesOperation ( callback ), this.session, item, AttributesHelper.toIce ( attributes ) );
        }
        catch ( final NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    public synchronized void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.listeners.add ( connectionStateListener );
    }

    protected synchronized void setState ( final ConnectionState state, final Throwable error )
    {
        if ( !this.state.equals ( state ) )
        {
            this.state = state;
            notifyStateChange ( state, error );
        }
    }

    private synchronized void notifyStateChange ( final ConnectionState state, final Throwable error )
    {
        for ( final ConnectionStateListener listener : this.listeners.toArray ( new ConnectionStateListener[0] ) )
        {
            listener.stateChange ( this, state, error );
        }
    }

    public void checkConnection ()
    {
        final SessionPrx session = this.session;

        if ( session == null )
        {
            return;
        }

        try
        {
            session.ice_ping ();
        }
        catch ( final Throwable e )
        {
            handleDisconnect ( e );
        }
    }

    public synchronized void connect ()
    {
        switch ( this.state )
        {
        case CLOSED:
            break;
        default:
            return;
        }

        this.connectionRequested = true;
        setState ( ConnectionState.CONNECTING, null );

        this.communicator = Util.initialize ( this.args, this.initData );
        // this.adapter = this.communicator.createObjectAdapter ( "Client" );
        this.adapter = this.communicator.createObjectAdapterWithEndpoints ( "Client", "tcp" );
        this.adapter.activate ();

        try
        {
            final ObjectPrx prx = this.communicator.stringToProxy ( getTarget () ).ice_secure ( isSecure () ).ice_timeout ( getTimeout () ).ice_twoway ();
            this.hive = HivePrxHelper.checkedCast ( prx );

            setState ( ConnectionState.CONNECTED, null );

            this.session = this.hive.createSession ( this.connectionInformation.getProperties () );

            // register data callback
            this.dataCallback = new DataCallbackImpl ( this );
            Ice.Identity ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            this.adapter.add ( this.dataCallback, ident );
            this.session.ice_getConnection ().setAdapter ( this.adapter );
            this.session.setDataCallback ( ident );

            // register folder callback
            this.folderCallback = new FolderCallbackImpl ( this );
            ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            this.adapter.add ( this.folderCallback, ident );
            this.session.ice_getConnection ().setAdapter ( this.adapter );
            this.session.setFolderCallback ( ident );

            setState ( ConnectionState.BOUND, null );
        }
        catch ( final Exception e )
        {
            handleDisconnect ( e );
        }
    }

    /**
     * Schedule a reconnect if a connection is currently requested
     * @param e the error that caused the disconnect
     */
    protected synchronized void handleDisconnect ( final Throwable e )
    {
        log.info ( "handleDisconnect", e );

        this.hive = null;
        this.session = null;
        this.adapter.deactivate ();
        this.communicator.destroy ();

        this.dataCallback = null;
        this.folderCallback = null;

        this.adapter = null;
        this.communicator = null;

        setState ( ConnectionState.CLOSED, e );

        if ( isAutoReconnect () && this.connectionRequested )
        {
            scheduleReconnect ();
        }
    }

    public synchronized void disconnect ()
    {
        switch ( this.state )
        {
        case BOUND:
            break;
        default:
            return;
        }

        log.debug ( "Shutting down connection" );
        this.connectionRequested = false;
        try
        {
            this.hive.closeSession ( this.session );
        }
        catch ( final Throwable e )
        {
            // don't care about this here
        }

        // now handle the disconnect
        handleDisconnect ( null );
    }

    public ConnectionState getState ()
    {
        return this.state;
    }

    public synchronized void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.listeners.remove ( connectionStateListener );
    }

    public void subscriptionChange ( final String item, final SubscriptionState subscriptionState )
    {
        synchronized ( this.eventQueue )
        {
            this.eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireSubscriptionChange ( item, subscriptionState );
                }
            } );
            this.eventQueue.notify ();
        }
    }

    public void dataChange ( final String itemId, final Variant value, final Map<String, Variant> attributes, final boolean full )
    {
        synchronized ( this.eventQueue )
        {
            this.eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireDataChange ( itemId, value, attributes, full );
                }
            } );
            this.eventQueue.notify ();
        }
    }

    public void folderChanged ( final Location location, final Entry[] entries, final String[] removed, final boolean full )
    {
        synchronized ( this.eventQueue )
        {
            this.eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireFolderChange ( location, entries, removed, full );
                }
            } );
            this.eventQueue.notify ();
        }
    }

    protected synchronized void fireDataChange ( final String itemId, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        final ItemUpdateListener listener = this.itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.notifyDataChange ( variant, attributes, cache );
                }
            } );

        }
    }

    protected synchronized void fireSubscriptionChange ( final String itemId, final SubscriptionState subscriptionState )
    {
        final ItemUpdateListener listener = this.itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.notifySubscriptionChange ( subscriptionState, null );
                }
            } );
        }
    }

    protected void fireFolderChange ( final Location location, final Entry[] added, final String[] removed, final boolean full )
    {
        final FolderListener listener = this.folderListenerMap.get ( location );
        if ( listener != null )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.folderChanged ( Arrays.asList ( added ), Arrays.asList ( removed ), full );
                }
            } );
        }
    }

    public synchronized ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        log.debug ( String.format ( "Setting listener for item '%s' to %s", itemId, "" + listener ) );
        return this.itemListenerMap.put ( itemId, listener );
    }

    public void subscribeItem ( final String itemId ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().subscribeItem ( this.session, itemId );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeItem ( final String itemId ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().unsubscribeItem ( this.session, itemId );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        synchronized ( this.folderListenerMap )
        {
            return this.folderListenerMap.put ( location, listener );
        }
    }

    public void subscribeFolder ( final Location location ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().subscribeFolder ( this.session, location.asArray () );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( final OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( final InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeFolder ( final Location location ) throws OperationException, NoConnectionException
    {
        try
        {
            getHive ().unsubscribeFolder ( this.session, location.asArray () );
        }
        catch ( final Ice.LocalException e )
        {
            handleDisconnect ( e );
            throw new OperationException ( e );
        }
        catch ( final InvalidSessionException e )
        {
            handleDisconnect ( e );
            throw new NoConnectionException ();
        }
        catch ( final OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( final InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }

    public Entry[] browse ( final Location location ) throws NoConnectionException, OperationException
    {
        return browse ( getHive (), location.asArray () );
    }

    public Entry[] browse ( final Location location, final int timeout ) throws NoConnectionException, OperationException
    {
        return browse ( HivePrxHelper.uncheckedCast ( getHive ().ice_timeout ( timeout ) ), location.asArray () );
    }

    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
        try
        {
            getHive ().browse_async ( new AsyncBrowseOperation ( callback ), this.session, location.asArray () );
        }
        catch ( final NoConnectionException e )
        {
            callback.error ( e );
        }
    }

    public void setExecutor ( final Executor executor )
    {
        this.executor = executor;
    }

    public Executor getExecutor ()
    {
        return this.executor;
    }
}
