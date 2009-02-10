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

package org.openscada.da.client.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.DriverFactory;
import org.openscada.core.client.DriverInformation;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.DisconnectReason;
import org.openscada.core.client.net.OperationTimedOutException;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.net.operations.BrowseOperationController;
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
import org.openscada.utils.exec.LongRunningState;
import org.openscada.utils.lang.Holder;

public class Connection extends ConnectionBase implements org.openscada.da.client.Connection
{
    static
    {
        ConnectionFactory.registerDriverFactory ( new DriverFactory () {

            public DriverInformation getDriverInformation ( final ConnectionInformation connectionInformation )
            {
                if ( !connectionInformation.getInterface ().equalsIgnoreCase ( "da" ) )
                {
                    return null;
                }
                if ( ! ( connectionInformation.getDriver ().equalsIgnoreCase ( "net" ) || connectionInformation.getDriver ().equalsIgnoreCase ( "gmpp" ) ) )
                {
                    return null;
                }

                if ( connectionInformation.getSecondaryTarget () == null )
                {
                    return null;
                }

                return new org.openscada.da.client.net.DriverInformation ();
            }
        } );
    }

    public static final String VERSION = "0.1.7";

    private static Logger _log = Logger.getLogger ( Connection.class );

    private final Map<String, ItemUpdateListener> _itemListeners = new HashMap<String, ItemUpdateListener> ();

    private final Map<Location, FolderListener> folderListeners = new HashMap<Location, FolderListener> ();

    //private List<ItemListListener> _itemListListeners = new ArrayList<ItemListListener> ();

    // operations
    private BrowseOperationController _browseController = null;

    private WriteOperationController _writeController = null;

    private WriteAttributesOperationController _writeAttributesController = null;

    public Connection ( final ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );

        init ();
    }

    @Override
    public ConnectionInformation getConnectionInformation ()
    {
        final ConnectionInformation info = new ConnectionInformation ();
        info.setInterface ( "da" );
        info.setDriver ( "net" );
        info.setTarget ( this.connectionInfo.getHostName () );
        info.setSecondaryTarget ( this.connectionInfo.getPort () );

        final Map<String, String> properties = new HashMap<String, String> ();
        if ( this.connectionInfo.getReconnectDelay () > 0 )
        {
            properties.put ( org.openscada.da.client.net.DriverInformation.PROP_AUTO_RECONNECT, "true" );
            properties.put ( org.openscada.da.client.net.DriverInformation.PROP_RECONNECT_DELAY, String.format ( "%s", this.connectionInfo.getReconnectDelay () ) );
        }

        info.setProperties ( properties );

        return info;
    }

    private void init ()
    {

        this.client.getMessageProcessor ().setHandler ( Messages.CC_NOTIFY_DATA, new MessageListener () {

            public void messageReceived ( final org.openscada.net.io.net.Connection connection, final Message message )
            {
                notifyDataChange ( message );
            }
        } );

        this.client.getMessageProcessor ().setHandler ( Messages.CC_BROWSER_EVENT, new MessageListener () {

            public void messageReceived ( final org.openscada.net.io.net.Connection connection, final Message message )
            {
                _log.debug ( "Browse event message from server" );
                performBrowseEvent ( message );
            }
        } );

        this.client.getMessageProcessor ().setHandler ( Messages.CC_SUBSCRIPTION_CHANGE, new MessageListener () {

            public void messageReceived ( final org.openscada.net.io.net.Connection connection, final Message message ) throws Exception
            {
                _log.debug ( "received subscription change" );
                performSubscriptionChange ( message );
            }
        } );

        this._browseController = new BrowseOperationController ( this.client );
        this._browseController.register ( this.client.getMessageProcessor () );

        this._writeController = new WriteOperationController ( this.client );
        this._writeController.register ( this.client.getMessageProcessor () );

        this._writeAttributesController = new WriteAttributesOperationController ( this.client );
        this._writeAttributesController.register ( this.client.getMessageProcessor () );
    }

    private void requestSession ()
    {
        if ( this.client == null )
        {
            return;
        }

        final Properties props = new Properties ();
        props.setProperty ( "client-version", VERSION );

        this.client.getConnection ().sendMessage ( Messages.createSession ( props ), new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                //setState ( ConnectionState.CLOSED, new OperationTimedOutException().fillInStackTrace () );
                disconnect ( new OperationTimedOutException ().fillInStackTrace () );
            }
        }, 10 * 1000 );
    }

    private void processSessionReply ( final Message message )
    {
        _log.debug ( "Got session reply!" );

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            final String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
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

    private void fireBrowseEvent ( final Location location, final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        final FolderListener listener;

        listener = this.folderListeners.get ( location );

        if ( listener != null )
        {
            try
            {
                listener.folderChanged ( added, removed, full );
            }
            catch ( final Throwable e )
            {
            }
        }
    }

    private void fireDataChange ( final String itemName, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final ItemUpdateListener listener = this._itemListeners.get ( itemName );
        if ( listener != null )
        {
            listener.notifyDataChange ( value, attributes, cache );
        }
    }

    private void notifyDataChange ( final Message message )
    {
        final boolean cache = message.getValues ().containsKey ( "cache-read" );
        final String itemId = message.getValues ().get ( "item-id" ).toString ();

        Variant value = decodeValueChange ( message );
        Map<String, Variant> attributes = decodeAttributeChange ( message );

        if ( cache && value == null )
        {
            // we need a value if we read from cache
            value = new Variant ();
        }
        if ( cache && attributes == null )
        {
            // we need attributes if we read from cache
            attributes = new HashMap<String, Variant> ();
        }

        fireDataChange ( itemId, value, attributes, cache );
    }

    /**
     * Decode the value change information from a "notify data" message 
     * @param message the message 
     * @return the decoded value or <code>null</code> if no value was encoded
     */
    private Variant decodeValueChange ( final Message message )
    {
        if ( message.getValues ().containsKey ( "value" ) )
        {
            return MessageHelper.valueToVariant ( message.getValues ().get ( "value" ), null );
        }
        return null;
    }

    /**
     * Decode the attributes from a "notify data" message
     * @param message the message
     * @return the decoded attributes or <code>null</code> if no attribute changed 
     */
    private Map<String, Variant> decodeAttributeChange ( final Message message )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        if ( message.getValues ().get ( "attributes-set" ) instanceof MapValue )
        {
            final MapValue setEntries = (MapValue)message.getValues ().get ( "attributes-set" );
            for ( final Map.Entry<String, Value> entry : setEntries.getValues ().entrySet () )
            {
                final Variant variant = Messages.valueToVariant ( entry.getValue (), null );
                if ( variant != null )
                {
                    attributes.put ( entry.getKey (), variant );
                }
            }
        }

        if ( message.getValues ().get ( "attributes-unset" ) instanceof ListValue )
        {
            final ListValue unsetEntries = (ListValue)message.getValues ().get ( "attributes-unset" );
            for ( final Value entry : unsetEntries.getValues () )
            {
                if ( entry instanceof StringValue )
                {
                    attributes.put ( ( (StringValue)entry ).getValue (), null );
                }
            }
        }

        if ( attributes.isEmpty () )
        {
            return null;
        }
        return attributes;
    }

    private void performBrowseEvent ( final Message message )
    {
        _log.debug ( "Performing browse event" );

        final List<Entry> added = new ArrayList<Entry> ();
        final List<String> removed = new ArrayList<String> ();
        final List<String> path = new ArrayList<String> ();
        final Holder<Boolean> initial = new Holder<Boolean> ();

        initial.value = false;

        ListBrowser.parseEvent ( message, path, added, removed, initial );

        final Location location = new Location ( path );

        _log.debug ( String.format ( "Folder: %1$s Added: %2$d Removed: %3$d", location.toString (), added.size (), removed.size () ) );

        fireBrowseEvent ( location, added, removed, initial.value );
    }

    // write operation

    public void write ( final String item, final Variant value ) throws NoConnectionException, OperationException
    {
        write ( item, value, null );
    }

    public void write ( final String item, final Variant value, final int timeout ) throws NoConnectionException, OperationException
    {
        final LongRunningOperation op = this._writeController.start ( item, value, null );
        try
        {
            op.waitForCompletion ( timeout );
        }
        catch ( final InterruptedException e )
        {
            throw new OperationException ( e );
        }
        completeWrite ( op );
    }

    public void write ( final String item, final Variant value, final WriteOperationCallback callback )
    {
        try
        {
            this._writeController.start ( item, value, new LongRunningListener () {

                public void stateChanged ( final LongRunningOperation operation, final LongRunningState state, final Throwable error )
                {
                    switch ( state )
                    {
                    case FAILURE:
                        if ( callback != null )
                        {
                            callback.failed ( error != null ? error.getMessage () : "<unknown error>" );
                        }
                        break;
                    case SUCCESS:
                        try
                        {
                            completeWrite ( operation );
                            if ( callback != null )
                            {
                                callback.complete ();
                            }
                        }
                        catch ( final OperationException e )
                        {
                            if ( callback != null )
                            {
                                callback.failed ( e.getMessage () );
                            }
                        }

                        break;
                    }
                }
            } );
        }
        catch ( final Exception e )
        {
            _log.info ( "Failed to write", e );
            if ( callback != null )
            {
                callback.error ( e );
            }
        }
    }

    protected void completeWrite ( final LongRunningOperation operation ) throws OperationException
    {
        if ( ! ( operation instanceof org.openscada.net.base.LongRunningOperation ) )
        {
            throw new RuntimeException ( "Operation is not of type org.openscada.net.base.LongRunningOperation" );
        }

        final org.openscada.net.base.LongRunningOperation op = (org.openscada.net.base.LongRunningOperation)operation;

        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            final Message reply = op.getReply ();
            if ( reply.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
            {
                throw new OperationException ( reply.getValues ().get ( Message.FIELD_ERROR_INFO ).toString () );
            }
        }
    }

    // write attributes operation
    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes ) throws NoConnectionException, OperationException
    {
        return writeAttributes ( itemId, attributes, 0 );
    }

    public WriteAttributeResults writeAttributes ( final String itemId, final Map<String, Variant> attributes, final int timeout ) throws NoConnectionException, OperationException
    {
        final LongRunningOperation op = this._writeAttributesController.start ( itemId, attributes, null );
        try
        {
            op.waitForCompletion ();
        }
        catch ( final InterruptedException e )
        {
            throw new OperationException ( e );
        }
        return completeWriteAttributes ( op );
    }

    public void writeAttributes ( final String item, final Map<String, Variant> attributes, final WriteAttributeOperationCallback callback )
    {
        try
        {
            this._writeAttributesController.start ( item, attributes, new LongRunningListener () {

                public void stateChanged ( final LongRunningOperation operation, final LongRunningState state, final Throwable error )
                {
                    switch ( state )
                    {
                    case FAILURE:
                        if ( callback != null )
                        {
                            callback.failed ( error.getMessage () );
                        }
                        break;
                    case SUCCESS:
                        try
                        {
                            final WriteAttributeResults results = completeWriteAttributes ( operation );
                            if ( callback != null )
                            {
                                callback.complete ( results );
                            }
                        }
                        catch ( final OperationException e )
                        {
                            if ( callback != null )
                            {
                                callback.failed ( e.getMessage () );
                            }
                        }

                        break;
                    }
                }
            } );
        }
        catch ( final Exception e )
        {
            callback.error ( e );
        }
    }

    public WriteAttributeResults completeWriteAttributes ( final LongRunningOperation operation ) throws OperationException
    {
        if ( ! ( operation instanceof org.openscada.net.base.LongRunningOperation ) )
        {
            throw new RuntimeException ( "Operation is not of type org.openscada.net.base.LongRunningOperation" );
        }

        final org.openscada.net.base.LongRunningOperation op = (org.openscada.net.base.LongRunningOperation)operation;

        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            final Message reply = op.getReply ();
            try
            {
                return WriteAttributesOperation.parseResponse ( reply );
            }
            catch ( final Exception e )
            {
                throw new OperationException ( e );
            }
        }
        return null;
    }

    public Entry[] browse ( final String[] path ) throws NoConnectionException, OperationException
    {
        return browse ( new Location ( path ) );
    }

    public Entry[] browse ( final String[] path, final int timeout ) throws OperationException
    {
        try
        {
            return browse ( new Location ( path ), timeout );
        }
        catch ( final NoConnectionException e )
        {
            throw new OperationException ( e );
        }
    }

    public void browse ( final String[] path, final BrowseOperationCallback callback )
    {
        browse ( new Location ( path ), callback );
    }

    protected Entry[] completeBrowse ( final LongRunningOperation operation ) throws OperationException
    {
        if ( ! ( operation instanceof org.openscada.net.base.LongRunningOperation ) )
        {
            throw new RuntimeException ( "Operation is not of type org.openscada.net.base.LongRunningOperation" );
        }

        final org.openscada.net.base.LongRunningOperation op = (org.openscada.net.base.LongRunningOperation)operation;

        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            final Message reply = op.getReply ();

            if ( reply.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
            {
                // in case of an error
                throw new OperationException ( reply.getValues ().get ( Message.FIELD_ERROR_INFO ).toString () );
            }
            else
            {
                // in case of success
                try
                {
                    return ListBrowser.parseResponse ( reply );
                }
                catch ( final Exception e )
                {
                    throw new OperationException ( e );
                }
            }

        }
        return null;
    }

    @Override
    protected void onConnectionBound ()
    {
    }

    @Override
    protected void onConnectionClosed ()
    {
    }

    @Override
    protected void onConnectionEstablished ()
    {
        requestSession ();
    }

    public void subscribeItem ( final String item ) throws NoConnectionException
    {
        sendMessage ( Messages.subscribeItem ( item ) );
    }

    public void unsubscribeItem ( final String itemId ) throws NoConnectionException
    {
        sendMessage ( Messages.unsubscribeItem ( itemId ) );
    }

    public ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        synchronized ( this._itemListeners )
        {
            return this._itemListeners.put ( itemId, listener );
        }
    }

    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        synchronized ( this.folderListeners )
        {
            return this.folderListeners.put ( location, listener );
        }
    }

    public void subscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        sendMessage ( ListBrowser.createSubscribe ( location.asArray () ) );
    }

    public void unsubscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        sendMessage ( ListBrowser.createUnsubscribe ( location.asArray () ) );
    }

    protected void performSubscriptionChange ( final Message message )
    {
        final Holder<String> item = new Holder<String> ();
        final Holder<SubscriptionState> subscriptionState = new Holder<SubscriptionState> ();

        Messages.parseSubscriptionChange ( message, item, subscriptionState );

        fireSubscriptionChange ( item.value, subscriptionState.value );
    }

    private void fireSubscriptionChange ( final String item, final SubscriptionState subscriptionState )
    {
        synchronized ( this._itemListeners )
        {
            final ItemUpdateListener listener = this._itemListeners.get ( item );
            if ( listener != null )
            {
                listener.notifySubscriptionChange ( subscriptionState, null );
            }
        }
    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalizing connection" );
        super.finalize ();
    }

    public Entry[] browse ( final Location location ) throws NoConnectionException, OperationException
    {
        return browse ( location, 0 );
    }

    public Entry[] browse ( final Location location, final int timeout ) throws NoConnectionException, OperationException
    {
        final LongRunningOperation op = this._browseController.start ( location.asArray (), null );
        try
        {
            op.waitForCompletion ( timeout );
            return completeBrowse ( op );
        }
        catch ( final InterruptedException e )
        {
            throw new OperationException ( e );
        }
    }

    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
        try
        {
            this._browseController.start ( location.asArray (), new LongRunningListener () {

                public void stateChanged ( final LongRunningOperation operation, final LongRunningState state, final Throwable error )
                {
                    switch ( state )
                    {
                    case FAILURE:
                        if ( callback != null )
                        {
                            callback.failed ( error.getMessage () );
                        }
                        break;
                    case SUCCESS:
                        try
                        {
                            final Entry[] result = completeBrowse ( operation );
                            if ( callback != null )
                            {
                                callback.complete ( result );
                            }
                        }
                        catch ( final OperationException e )
                        {
                            if ( callback != null )
                            {
                                callback.failed ( e.getMessage () );
                            }
                        }
                        break;
                    }
                }
            } );
        }
        catch ( final Exception e )
        {
            callback.error ( e );
        }
    }
}
