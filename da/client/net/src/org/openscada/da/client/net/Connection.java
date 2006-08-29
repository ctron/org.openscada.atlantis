/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.client.net.operations.BrowserListOperation;
import org.openscada.da.client.net.operations.OperationException;
import org.openscada.da.client.net.operations.WriteAttributesOperationController;
import org.openscada.da.client.net.operations.WriteOperationController;
import org.openscada.da.core.Location;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;
import org.openscada.da.core.server.browser.Entry;
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.EnumEvent;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.da.handler.WriteAttributesOperation;
import org.openscada.net.io.IOProcessor;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;
import org.openscada.utils.lang.Holder;

public class Connection
{

    public static final String VERSION = "0.1.5";

    public enum State
    {
        CLOSED,
        LOOKUP,
        CONNECTING,
        CONNECTED,
        BOUND,
        CLOSING,
    }

    private static Logger _log = Logger.getLogger ( Connection.class );

    private ConnectionInfo _connectionInfo = null;
    private SocketAddress _remote = null;
    private IOProcessor _processor = null;

    private ClientConnection _client = null;

    private List<ConnectionStateListener> _connectionStateListeners = new ArrayList<ConnectionStateListener> ();
    private Map<String, ItemSyncController> _itemListeners = new HashMap<String, ItemSyncController> ();
    private Map<Location, FolderSyncController> _folderListeners = new HashMap<Location, FolderSyncController> ();

    //private boolean _connected = false;
    private State _state = State.CLOSED;

    private static Object _defaultProcessorLock = new Object ();
    private static IOProcessor _defaultProcessor = null;

    private ItemList _itemList = new ItemList ();
    private List<ItemListListener> _itemListListeners = new ArrayList<ItemListListener> ();

    // operations
    private BrowserListOperation _browseListOperation;
    
    private WriteOperationController _writeController = null;
    private WriteAttributesOperationController _writeAttributesController = null;

    private static IOProcessor getDefaultProcessor ()
    {
        try
        {
            synchronized ( _defaultProcessorLock )
            {
                if ( _defaultProcessor == null )
                {
                    _defaultProcessor = new IOProcessor ();
                    _defaultProcessor.start();
                }
                return _defaultProcessor;
            }
        }
        catch ( IOException e )
        {    
            _log.error ( "unable to created io processor", e );
        }
        // operation failed
        return null;
    }

    public Connection ( IOProcessor processor, ConnectionInfo connectionInfo )
    {
        super();

        _processor = processor;
        _connectionInfo = connectionInfo;

        // register our own list
        addItemListListener ( _itemList );

        init ();

        _browseListOperation = new BrowserListOperation ( this );
    }

    public Connection ( ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor(), connectionInfo );
    }

    private void init ()
    {
        if ( _client != null )
            return;

        _client = new ClientConnection ( _processor );
        _client.addStateListener(new  org.openscada.net.io.ConnectionStateListener(){

            public void closed ( Exception error )
            {
                _log.debug ( "closed" );
                fireDisconnected ( error );
            }

            public void opened ()
            {
                _log.debug ( "opened" );
                fireConnected ();
            }});

        _client.getMessageProcessor().setHandler(Messages.CC_NOTIFY_VALUE, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.net.Connection connection, Message message )
            {
                notifyValueChange(message);
            }} );

        _client.getMessageProcessor().setHandler(Messages.CC_NOTIFY_ATTRIBUTES, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.net.Connection connection, Message message )
            {
                notifyAttributesChange(message);
            }});

        _client.getMessageProcessor().setHandler(Messages.CC_ENUM_EVENT, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.net.Connection connection, Message message )
            {
                _log.debug("Enum message from server");
                performEnumEvent ( message );
            }});
        
        _client.getMessageProcessor().setHandler(Messages.CC_BROWSER_EVENT, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.net.Connection connection, Message message )
            {
                _log.debug("Browse event message from server");
                performBrowseEvent ( message );
            }});

        _writeController = new WriteOperationController ( _client );
        _writeController.register ( _client.getMessageProcessor () );
        
        _writeAttributesController = new WriteAttributesOperationController ( _client );
        _writeAttributesController.register ( _client.getMessageProcessor () );
    }

    synchronized public void connect ()
    {
        switch ( _state )
        {
        case CLOSED:
            setState ( State.CONNECTING, null );
            break;
        }        
    }

    synchronized public void disconnect ()
    {
        disconnect ( null );
    }

    synchronized private void disconnect ( Throwable reason )
    {
        switch ( _state )
        {
        case LOOKUP:
            setState ( State.CLOSED, reason );
            break;
            
        case BOUND:
        case CONNECTING:
        case CONNECTED:
            setState ( State.CLOSING, reason );
            break;
        }    
    }
    
    public void sendMessage ( Message message )
    {
        if ( _client == null )
            return;
        if ( _client.getConnection () == null )
            return;
        
        _client.getConnection ().sendMessage ( message );
    }
    
    public void sendMessage ( Message message, MessageStateListener listener, long timeout )
    {
        if ( _client == null )
            return;
        if ( _client.getConnection () == null )
            return;
        
        _client.getConnection ().sendMessage ( message, listener, timeout );
    }

    public void addItemListListener ( ItemListListener listener )
    {
        synchronized ( _itemListListeners )
        {
            _itemListListeners.add ( listener );
        }
    }

    public void removeItemListListener ( ItemListListener listener )
    {
        synchronized ( _itemListListeners )
        {
            _itemListListeners.remove ( listener );
        }
    }

    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.add ( connectionStateListener );
        }
    }

    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.remove ( connectionStateListener );
        }
    }

    private void fireConnected ()
    {
        _log.debug ( "connected" );

        switch ( _state )
        {
        case CONNECTING:
            setState ( State.CONNECTED, null );
            break;
        }

    }

    private void fireDisconnected ( Throwable error )
    {
        _log.debug ( "dis-connected" );

        switch ( _state )
        {
        case BOUND:
        case CONNECTED:
        case CONNECTING:
        case LOOKUP:
        case CLOSING:
            setState ( State.CLOSED, error );
            break;
        }

    }

    private void fireItemListChange ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        synchronized ( _itemListListeners )
        {
            _log.debug("Sending out enum events");

            for ( ItemListListener listener : _itemListListeners )
            {
                try {
                    listener.changed ( added, removed, initial );
                }
                catch ( Exception e )
                {}
            }
        }
    }

    private void requestSession ()
    {
        if ( _client == null )
            return;

        Properties props = new Properties();
        props.setProperty ( "client-version", VERSION );

        _client.getConnection().sendMessage ( Messages.createSession ( props ), new MessageStateListener(){

            public void messageReply ( Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                //setState ( State.CLOSED, new OperationTimedOutException().fillInStackTrace () );
                disconnect (  new OperationTimedOutException().fillInStackTrace () );
            }}, 10 * 1000 );
    }

    private void processSessionReply ( Message message )
    {
        _log.debug ( "Got session reply!" );

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            disconnect ( new DisconnectReason ( "Failed to create session: " + errorInfo ) );
        }
        else if ( message.getCommandCode () != Message.CC_ACK )
        {
            disconnect ( new DisconnectReason ( "Received an invalid reply when requesting session" ) );
        }
        else
        {
            setState ( State.BOUND, null );

        }
    }

    public void addFolderListener ( FolderListener listener, Location location )
    {
        synchronized ( _folderListeners )
        {
            if ( !_folderListeners.containsKey ( location ) )
            {
                _folderListeners.put ( location, new FolderSyncController ( this, new Location ( location ) ) );
            }
            
            FolderSyncController controller = _folderListeners.get ( location );
            controller.addListener ( listener );
        }    
    }
    
    public void addFolderWatcher ( FolderWatcher watcher )
    {
        addFolderListener ( watcher, watcher.getLocation () );
    }
    
    public void removeFolderListener ( FolderListener listener, Location location )
    {
        synchronized ( _folderListeners )
        {
            if ( !_folderListeners.containsKey ( location ) )
            {
                return;
            }
            
            FolderSyncController controller = _folderListeners.get ( location );
            controller.removeListener ( listener );
        }    
    }
    
    public void removeFolderWatcher ( FolderWatcher watcher )
    {
        removeFolderListener ( watcher, watcher.getLocation () );
    }
    
    public void addItemUpdateListener ( String itemName, boolean initial, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if ( !_itemListeners.containsKey ( itemName ) )
            {
                _itemListeners.put ( itemName, new ItemSyncController ( this, new String ( itemName ) ) );
            }

            ItemSyncController controller = _itemListeners.get ( itemName );
            controller.add ( listener, initial );
        }
    }

    public void removeItemUpdateListener ( String itemName, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if ( !_itemListeners.containsKey ( itemName ) )
            {
                return;
            }

            ItemSyncController controller = _itemListeners.get ( itemName );
            controller.remove ( listener );
        }
    }

    /**
     * Synchronized all items that are currently known
     *
     */
    private void resyncAllItems ()
    {
        _log.debug("Syncing all items");

        synchronized ( _itemListeners )
        {
            for ( Map.Entry<String,ItemSyncController> entry : _itemListeners.entrySet() )
            {
                entry.getValue().sync ( true );
            }
        }
        _log.debug("re-sync complete");
    }

    private void resyncAllFolders ()
    {
        synchronized ( _folderListeners )
        {
            for ( Map.Entry<Location,FolderSyncController> entry : _folderListeners.entrySet () )
            {
                entry.getValue ().resync ();
            }
        }
    }
    
    private void disconnectAllFolders ()
    {
        synchronized ( _folderListeners )
        {
            for ( Map.Entry<Location,FolderSyncController> entry : _folderListeners.entrySet () )
            {
                entry.getValue ().disconnected ();
            }
        }
    }
    
    private void fireBrowseEvent ( Location location, Collection<Entry> added, Collection<String> removed, boolean full )
    {
        synchronized ( _folderListeners )
        {
            if ( _folderListeners.containsKey ( location ) )
            {
                try
                {
                    _folderListeners.get ( location ).folderChanged ( added, removed, full );
                }
                catch ( Exception e )
                {}
            }
        }
    }

    private void fireValueChange ( String itemName, Variant value, boolean initial )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey ( itemName ) )
            {
                _itemListeners.get ( itemName ).fireValueChange ( value, initial );
            }
        }
    }

    private void fireAttributesChange ( String itemName, Map<String,Variant> attributes, boolean initial )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey ( itemName ) )
            {
                _itemListeners.get ( itemName ).fireAttributesChange(attributes,initial);
            }
        }
    }

    private void notifyValueChange ( Message message )
    {
        Variant value = new Variant ();

        // extract initial bit
        boolean initial = message.getValues().containsKey("initial");


        if ( message.getValues().containsKey("value") )
        {
            value = Messages.valueToVariant ( message.getValues().get("value"), null );
        }

        String itemName = message.getValues().get("item-name").toString();
        fireValueChange(itemName, value, initial);
    }



    private void notifyAttributesChange ( Message message )
    {
        Map<String,Variant> attributes = new HashMap<String,Variant>();

        // extract initial bit
        boolean initial = message.getValues().containsKey("initial");
        
        if ( message.getValues ().get ( "set" ) instanceof MapValue )
        {
            MapValue setEntries = (MapValue)message.getValues ().get ( "set" );
            for ( Map.Entry<String,Value> entry : setEntries.getValues ().entrySet () )
            {
                Variant variant = Messages.valueToVariant ( entry.getValue (), null );
                if ( variant != null )
                    attributes.put ( entry.getKey (), variant );
            }
        }
        
        if ( message.getValues ().get ( "unset" ) instanceof ListValue )
        {
            ListValue unsetEntries = (ListValue)message.getValues ().get ( "unset" );
            for ( Value entry : unsetEntries.getValues () )
            {
                if ( entry instanceof StringValue )
                    attributes.put ( ((StringValue)entry).getValue (), null );
            }
        }

        String itemName = message.getValues().get("item-name").toString();
        fireAttributesChange ( itemName, attributes, initial );
    }

    private void performEnumEvent ( Message message )
    {
        synchronized ( _itemList )
        {
            List<DataItemInformation> added = new ArrayList<DataItemInformation> ();
            List<String> removed = new ArrayList<String> ();
            Holder<Boolean> initial = new Holder<Boolean> ();

            EnumEvent.parse ( message, added, removed, initial );

            fireItemListChange ( added, removed, initial.value.booleanValue() );
        }
    }
    
    private void performBrowseEvent ( Message message )
    {
        _log.debug ( "Performing browse event" );
        
        synchronized ( _itemList )
        {
            List<Entry> added = new ArrayList<Entry> ();
            List<String> removed = new ArrayList<String> ();
            List<String> path = new ArrayList<String> ();
            Holder<Boolean> initial = new Holder<Boolean> ();
            
            initial.value = false;

            ListBrowser.parseEvent ( message, path, added, removed, initial );
            
            Location location = new Location ( path );
            
            _log.debug ( String.format ( "Folder: %1$s Added: %2$d Removed: %3$d", location.toString (), added.size (), removed.size() ) );

            fireBrowseEvent ( location, added, removed, initial.value );
        }
    }

    public State getState ()
    {
        return _state;
    }

    /**
     * Get the network client
     * @return the client instance of <em>null</em> if the client has not been started
     */
    public ClientConnection getClient ()
    {
        return _client;
    }

    /**
     * Get the item list. This list is maintained by the connection and will be
     * feeded with events.
     * @return the dynamic item list
     */
    public ItemList getItemList ()
    {
        synchronized ( _itemList )
        {
            return _itemList;
        }
    }
    
    // write operation
    
    public void write ( String itemName, Variant value ) throws InterruptedException, OperationException
    {
        write ( itemName, value, null );
    }
    
    public void write ( String itemName, Variant value, Listener listener ) throws InterruptedException, OperationException
    {
        LongRunningOperation op = startWrite ( itemName, value, listener );
        synchronized ( op )
        {
            op.wait ();
            completeWrite ( op );
        }
    }
    
    public LongRunningOperation startWrite ( String itemName, Variant value, Listener listener )
    {
        return _writeController.start ( itemName, value, listener );   
    }
    
    public void completeWrite ( LongRunningOperation op ) throws OperationException
    {
        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            Message reply = op.getReply ();
            if ( reply.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
            {
                throw new OperationException ( reply.getValues ().get ( Message.FIELD_ERROR_INFO ).toString () );
            }
        }
    }
    
    // write attributes operation
    public void writeAttributes ( String itemId, Map<String,Variant> attributes ) throws InterruptedException, OperationException
    {
        write ( itemId, attributes, null );
    }
    
    public void write ( String itemId, Map<String,Variant> attributes, Listener listener ) throws InterruptedException, OperationException
    {
        LongRunningOperation op = startWriteAttributes ( itemId, attributes, listener );
        synchronized ( op )
        {
            op.wait ();
            completeWriteAttributes ( op );
        }
    }
    
    public LongRunningOperation startWriteAttributes ( String itemId, Map<String,Variant> attributes, Listener listener )
    {
        return _writeAttributesController.start ( itemId, attributes, listener );   
    }
    
    public Results completeWriteAttributes ( LongRunningOperation op ) throws OperationException
    {
        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            Message reply = op.getReply ();
            try
            {
                return WriteAttributesOperation.parseResponse ( reply );
            }
            catch ( Exception e )
            {
                throw new OperationException ( e );
            }
        }
        return null;
    }
    
    public Entry[] browse ( String [] path ) throws Exception
    {
        return _browseListOperation.execute ( path );
    }

    public OperationResult<Entry[]> startBrowse ( String [] path, Variant value )
    {
        return _browseListOperation.startExecute ( path );
    }

    public OperationResult<Entry[]> startBrowse ( String [] path, OperationResultHandler<Entry[]> handler )
    {
        return _browseListOperation.startExecute ( handler, path );
    }

    /**
     * set new state internaly
     * @param state
     * @param error additional error information or <code>null</code> if we don't have an error.
     */
    synchronized private void setState ( State state, Throwable error )
    {
        _state = state;

        stateChanged ( state, error );
    }

    private void stateChanged ( State state, Throwable error )
    {
        switch ( state )
        {

        case CLOSED:
            // inform folder sync controllers 
            disconnectAllFolders ();
            
            // if we got the close and are auto-reconnect ... schedule the job
            if ( _connectionInfo.isAutoReconnect () )
            {
                _processor.getScheduler ().scheduleJob ( new Runnable() {

                    public void run ()
                    {
                        connect ();
                    }}, _connectionInfo.getReconnectDelay () );
            }
            break;

        case CONNECTING:
            performConnect ();
            break;
            
        case LOOKUP:
            break;
            
        case CONNECTED:
            requestSession ();
            break;

        case BOUND:
            // sync again all items to maintain subscribtions
            resyncAllItems ();
            
            // subscribe enum service
            // subscribeEnum ();
            
            // sync again all folder subscriptions
            resyncAllFolders ();
            break;

        case CLOSING:
            _client.disconnect ();
            break;
        }

        notifyStateChange ( state, error );

    }



    /**
     * Notify state change listeners
     * @param state new state
     * @param error additional error information or <code>null</code> if we don't have an error. 
     */
    private void notifyStateChange ( State state, Throwable error )
    {   
        List<ConnectionStateListener> connectionStateListeners;

        synchronized ( _connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener> ( _connectionStateListeners );
        }
        for ( ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.stateChange ( this, state, error );
            }
            catch ( Exception e )
            {
            }
        }
    }

    synchronized private void performConnect ()
    {
        if ( _remote != null )
        {
            _client.connect ( _remote );
        }
        else
        {
            setState ( State.LOOKUP, null );
            Thread lookupThread = new Thread ( new Runnable() {

                public void run ()
                {
                    performLookupAndConnect ();
                }} );
            lookupThread.setDaemon ( true );
            lookupThread.start ();
        }
    }

    private void performLookupAndConnect ()
    {
        // lookup may take some time
        try
        {
            SocketAddress remote = new InetSocketAddress ( InetAddress.getByName ( _connectionInfo.getHostName () ), _connectionInfo.getPort () );
            _remote = remote;
            // this time "remote" should not be null
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) )
                    setState ( State.CONNECTING, null );
            }
        }
        catch ( UnknownHostException e )
        {
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) ) 
                    setState ( State.CLOSED, e );
            }
        } 
    }
    
   
}
