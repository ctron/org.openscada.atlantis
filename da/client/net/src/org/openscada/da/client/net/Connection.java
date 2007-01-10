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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.DisconnectReason;
import org.openscada.core.client.net.OperationTimedOutException;
import org.openscada.core.net.MessageHelper;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.FolderWatcher;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.net.operations.BrowserListOperation;
import org.openscada.da.client.net.operations.WriteAttributesOperationController;
import org.openscada.da.client.net.operations.WriteOperationController;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.da.handler.WriteAttributesOperation;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;
import org.openscada.utils.lang.Holder;

public class Connection extends ConnectionBase implements org.openscada.da.client.Connection
{

    public static final String VERSION = "0.1.5";

    private static Logger _log = Logger.getLogger ( Connection.class );

    private Map<String, ItemSyncController> _itemListeners = new HashMap<String, ItemSyncController> ();
    private Map<Location, FolderSyncController> _folderListeners = new HashMap<Location, FolderSyncController> ();

    private List<ItemListListener> _itemListListeners = new ArrayList<ItemListListener> ();

    // operations
    private BrowserListOperation _browseListOperation;
    
    private WriteOperationController _writeController = null;
    private WriteAttributesOperationController _writeAttributesController = null;

    public Connection ( ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );
        
        init ();
    }
    
    private void init ()
    {
       
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
                //setState ( ConnectionState.CLOSED, new OperationTimedOutException().fillInStackTrace () );
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
            setState ( ConnectionState.BOUND, null );

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
        boolean initial = message.getValues().containsKey("cache-read");


        if ( message.getValues().containsKey("value") )
        {
            value = MessageHelper.valueToVariant ( message.getValues().get("value"), null );
        }

        String itemName = message.getValues().get("item-id").toString();
        fireValueChange(itemName, value, initial);
    }



    private void notifyAttributesChange ( Message message )
    {
        Map<String,Variant> attributes = new HashMap<String,Variant>();

        // extract initial bit
        boolean initial = message.getValues().containsKey("cache-read");
        
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

        String itemName = message.getValues().get("item-id").toString();
        fireAttributesChange ( itemName, attributes, initial );
    }

    private void performBrowseEvent ( Message message )
    {
        _log.debug ( "Performing browse event" );
        
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

    // write operation
    
    public void write ( String itemName, Variant value ) throws InterruptedException, OperationException
    {
        write ( itemName, value, null );
    }
    
    public void write ( String itemName, Variant value, LongRunningListener listener ) throws InterruptedException, OperationException
    {
        LongRunningOperation op = startWrite ( itemName, value, listener );
        op.waitForCompletion ();
        completeWrite ( op );
    }
    
    public LongRunningOperation startWrite ( String itemName, Variant value, LongRunningListener listener )
    {
        return _writeController.start ( itemName, value, listener );   
    }
    
    public void completeWrite ( LongRunningOperation operation ) throws OperationException
    {
        if ( !(operation instanceof org.openscada.net.base.LongRunningOperation ) )
            throw new RuntimeException ( "Operation is not of type org.openscada.net.base.LongRunningOperation" );
        
        org.openscada.net.base.LongRunningOperation op = (org.openscada.net.base.LongRunningOperation)operation;
        
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
        writeAttributes ( itemId, attributes, null );
    }
    
    public void writeAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener ) throws InterruptedException, OperationException
    {
        LongRunningOperation op = startWriteAttributes ( itemId, attributes, listener );
        op.waitForCompletion ();
        completeWriteAttributes ( op );
    }
    
    public LongRunningOperation startWriteAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener )
    {
        return _writeAttributesController.start ( itemId, attributes, listener );   
    }
    
    public WriteAttributeResults completeWriteAttributes ( LongRunningOperation operation ) throws OperationException
    {
        if ( !(operation instanceof org.openscada.net.base.LongRunningOperation ) )
            throw new RuntimeException ( "Operation is not of type org.openscada.net.base.LongRunningOperation" );
        
        org.openscada.net.base.LongRunningOperation op = (org.openscada.net.base.LongRunningOperation)operation;
        
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

    @Override
    protected void onConnectionBound ()
    {
        resyncAllItems ();
        // sync again all folder subscriptions
        resyncAllFolders ();   
    }

    @Override
    protected void onConnectionClosed ()
    {
        disconnectAllFolders ();
    }

    @Override
    protected void onConnectionEstablished ()
    {
       requestSession ();
    }
    
   
}
