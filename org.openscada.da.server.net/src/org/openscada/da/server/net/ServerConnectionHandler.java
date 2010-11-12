/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.net.handler.ListBrowser;
import org.openscada.da.net.handler.Messages;
import org.openscada.da.net.handler.WriteAttributesOperation;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.concurrent.ResultHandler;
import org.openscada.utils.concurrent.task.DefaultTaskHandler;
import org.openscada.utils.concurrent.task.ResultFutureHandler;
import org.openscada.utils.concurrent.task.TaskHandler;
import org.openscada.utils.lang.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnectionHandler extends AbstractServerConnectionHandler implements ItemChangeListener, FolderListener
{

    public final static String VERSION = "0.1.8";

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnectionHandler.class );

    private Hive hive = null;

    private Session session = null;

    private final TaskHandler taskHandler = new DefaultTaskHandler ();

    private final Set<Long> taskMap = new HashSet<Long> ();

    public ServerConnectionHandler ( final Hive hive, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.hive = hive;

        this.messenger.setHandler ( MessageHelper.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                createSession ( message );
            }
        } );

        this.messenger.setHandler ( MessageHelper.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                closeSession ();
            }
        } );

        this.messenger.setHandler ( Messages.CC_SUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                subscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_UNSUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                unsubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_WRITE_OPERATION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performWrite ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_WRITE_ATTRIBUTES_OPERATION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performWriteAttributes ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_LIST_REQ, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowse ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_SUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowserSubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_UNSUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowserUnsubscribe ( message );
            }
        } );
    }

    private void createSession ( final Message message )
    {
        // if session exists this is an error
        if ( this.session != null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        // get the session properties
        final Properties props = new Properties ();
        MessageHelper.getProperties ( props, message.getValues ().get ( "properties" ) );

        // now check client version
        final String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion + "' server: '" + VERSION + "'" ) );
            return;
        }

        try
        {
            this.session = (Session)this.hive.createSession ( props );
        }
        catch ( final UnableToCreateSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // we have a working session .. so connect listeners
        this.session.setListener ( (ItemChangeListener)this );
        this.session.setListener ( (FolderListener)this );

        // send success
        this.messenger.sendMessage ( MessageHelper.createSessionACK ( message, this.session.getProperties () ) );
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            try
            {
                this.hive.closeSession ( this.session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    private void subscribe ( final Message message )
    {
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        final String itemName = message.getValues ().get ( "item-id" ).toString ();
        final boolean initial = message.getValues ().containsKey ( "cache-read" );

        logger.debug ( "Subscribe to {} initial {}", itemName, initial );

        try
        {
            this.hive.subscribeItem ( this.session, itemName );
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( final InvalidItemException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }

    }

    private void unsubscribe ( final Message message )
    {
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        final String itemName = message.getValues ().get ( "item-id" ).toString ();

        try
        {
            this.hive.unsubscribeItem ( this.session, itemName );
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( final InvalidItemException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }
    }

    @Override
    protected void cleanUp ()
    {
        super.cleanUp ();
        disposeSession ();
    }

    public void dataChanged ( final String itemId, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        logger.debug ( "Data changed - itemId: {}, value: {}, attributes: {}, cache: {}", new Object[] { itemId, value, attributes, cache } );

        this.messenger.sendMessage ( Messages.notifyData ( itemId, value, attributes, cache ) );
    }

    public void subscriptionChanged ( final String item, final SubscriptionState subscriptionState )
    {
        this.messenger.sendMessage ( Messages.notifySubscriptionChange ( item, subscriptionState ) );
    }

    private void performWrite ( final Message request )
    {
        final Holder<String> itemId = new Holder<String> ();
        final Holder<Variant> value = new Holder<Variant> ();

        org.openscada.da.net.handler.WriteOperation.parse ( request, itemId, value );

        try
        {
            final NotifyFuture<WriteResult> task = this.hive.startWrite ( this.session, itemId.value, value.value );
            final TaskHandler.Handle handle = this.taskHandler.addTask ( task );

            try
            {
                final Message reply = MessageCreator.createACK ( request );
                reply.getValues ().put ( "id", new LongValue ( handle.getId () ) );
                this.messenger.sendMessage ( reply );
            }
            catch ( final Throwable e )
            {
                task.cancel ( true );
                throw e;
            }

            scheduleTask ( task, handle.getId (), new ResultHandler<WriteResult> () {

                public void completed ( final WriteResult result )
                {
                    final Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
                    replyMessage.getValues ().put ( "id", new LongValue ( handle.getId () ) );
                    ServerConnectionHandler.this.messenger.sendMessage ( replyMessage );
                    handle.dispose ();
                }

                public void failed ( final Throwable e )
                {
                    final Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
                    replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( e.getMessage () ) );
                    replyMessage.getValues ().put ( "id", new LongValue ( handle.getId () ) );
                    ServerConnectionHandler.this.messenger.sendMessage ( replyMessage );
                    handle.dispose ();
                }
            } );

        }
        catch ( final Throwable e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( request, e ) );
        }
    }

    private <T> void scheduleTask ( final NotifyFuture<T> task, final long id, final ResultHandler<T> resultHandler )
    {
        task.addListener ( new ResultFutureHandler<T> ( resultHandler ) );
    }

    private void removeTask ( final long id )
    {
        synchronized ( this.taskMap )
        {
            this.taskMap.remove ( id );
        }
    }

    private void performWriteAttributes ( final Message request )
    {
        final Holder<String> itemId = new Holder<String> ();
        final Holder<Map<String, Variant>> attributes = new Holder<Map<String, Variant>> ();

        WriteAttributesOperation.parseRequest ( request, itemId, attributes );

        try
        {
            final NotifyFuture<WriteAttributeResults> task = this.hive.startWriteAttributes ( this.session, itemId.value, attributes.value );
            final TaskHandler.Handle handle = this.taskHandler.addTask ( task );

            try
            {
                final Message reply = MessageCreator.createACK ( request );
                reply.getValues ().put ( "id", new LongValue ( handle.getId () ) );
                this.messenger.sendMessage ( reply );
            }
            catch ( final Throwable e )
            {
                task.cancel ( true );
                throw e;
            }

            scheduleTask ( task, handle.getId (), new ResultHandler<WriteAttributeResults> () {

                public void completed ( final WriteAttributeResults result )
                {
                    final Message message = WriteAttributesOperation.createResponse ( handle.getId (), result );
                    ServerConnectionHandler.this.messenger.sendMessage ( message );
                    handle.dispose ();
                }

                public void failed ( final Throwable e )
                {
                    final Message message = WriteAttributesOperation.createResponse ( handle.getId (), e );
                    ServerConnectionHandler.this.messenger.sendMessage ( message );
                    handle.dispose ();
                }
            } );

        }
        catch ( final Throwable e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( request, e ) );
        }
    }

    private void performBrowse ( final Message request )
    {
        final String[] location = ListBrowser.parseRequest ( request );

        final HiveBrowser browser = this.hive.getBrowser ();
        if ( browser == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( request, "Browsing not supported" ) );
            return;
        }

        try
        {
            final NotifyFuture<Entry[]> task = browser.startBrowse ( this.session, new Location ( location ) );

            final TaskHandler.Handle handle = this.taskHandler.addTask ( task );

            try
            {
                final Message reply = MessageCreator.createACK ( request );
                reply.getValues ().put ( "id", new LongValue ( handle.getId () ) );
                this.messenger.sendMessage ( reply );
            }
            catch ( final Throwable e )
            {
                removeTask ( handle.getId () );
                task.cancel ( true );
                throw e;
            }

            scheduleTask ( task, handle.getId (), new ResultHandler<Entry[]> () {

                public void completed ( final Entry[] result )
                {
                    ServerConnectionHandler.this.messenger.sendMessage ( ListBrowser.createResponse ( handle.getId (), result ) );
                    handle.dispose ();
                }

                public void failed ( final Throwable e )
                {
                    ServerConnectionHandler.this.messenger.sendMessage ( ListBrowser.createResponse ( handle.getId (), e.getMessage () ) );
                    handle.dispose ();
                }
            } );

        }
        catch ( final Throwable e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( request, e ) );
        }
    }

    public void folderChanged ( final Location location, final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        logger.debug ( "Got folder change event from hive for folder: {}", location );
        this.messenger.sendMessage ( ListBrowser.createEvent ( location.asArray (), added, removed, full ) );
    }

    private void performBrowserSubscribe ( final Message message )
    {
        final HiveBrowser browser = this.hive.getBrowser ();

        if ( browser == null )
        {
            logger.warn ( "Unable to subscribe to folder: no hive browser set" );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        final Location location = new Location ( ListBrowser.parseSubscribeMessage ( message ) );

        try
        {
            logger.debug ( "Subscribe to folder: {}", location.toString () );
            browser.subscribe ( this.session, location );
        }
        catch ( final NoSuchFolderException e )
        {
            logger.warn ( "Unable to subscribe to folder: " + location, e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( final InvalidSessionException e )
        {
            logger.warn ( "Unable to subscribe to folder: " + location, e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Browsing failed", e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
            return;
        }
    }

    private void performBrowserUnsubscribe ( final Message message )
    {
        final HiveBrowser browser = this.hive.getBrowser ();

        if ( browser == null )
        {
            logger.warn ( "Unable to unsubscribe from folder: no hive browser set" );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        final Location location = new Location ( ListBrowser.parseUnsubscribeMessage ( message ) );

        try
        {
            logger.debug ( "Unsubscribe from folder: {}", location.toString () );
            browser.unsubscribe ( this.session, location );
        }
        catch ( final NoSuchFolderException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
    }

}
