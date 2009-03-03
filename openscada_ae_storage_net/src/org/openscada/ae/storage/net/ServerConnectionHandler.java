/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.storage.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.NoSuchQueryException;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.ae.net.EventMessage;
import org.openscada.ae.net.ListMessage;
import org.openscada.ae.net.Messages;
import org.openscada.ae.net.SubmitEventMessage;
import org.openscada.ae.net.SubscribeMessage;
import org.openscada.ae.net.UnsubscribeMessage;
import org.openscada.ae.net.UnsubscribedMessage;
import org.openscada.ae.storage.net.controller.ListController;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.utils.MessageCreator;

public class ServerConnectionHandler extends AbstractServerConnectionHandler implements QueryListener
{

    public final static String VERSION = "0.1.0";

    private static Logger log = Logger.getLogger ( ServerConnectionHandler.class );

    private Storage storage = null;

    private Session session = null;

    private final Map<Long, EventListener> listenerMap = new HashMap<Long, EventListener> ();

    public ServerConnectionHandler ( final Storage storage, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.storage = storage;

        this.messenger.setHandler ( Messages.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                createSession ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                closeSession ();
            }
        } );

        this.messenger.setHandler ( Messages.CC_LIST, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                performList ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_SUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                performSubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_UNSUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                performUnsubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_SUBMIT_EVENT, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                performSubmitEvent ( message );
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

        final Properties props = new Properties ();
        for ( final Map.Entry<String, Value> entry : message.getValues ().getValues ().entrySet () )
        {
            props.put ( entry.getKey (), entry.getValue ().toString () );
        }

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
            this.session = this.storage.createSession ( props );
        }
        catch ( final UnableToCreateSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e.getReason () ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // send success
        this.messenger.sendMessage ( MessageCreator.createACK ( message ) );
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            try
            {
                this.storage.closeSession ( this.session );
            }
            catch ( final InvalidSessionException e )
            {
                e.printStackTrace ();
            }
        }
        this.messenger.disconnected ();
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    @Override
    protected void cleanUp ()
    {
        disposeSession ();
        super.cleanUp ();
    }

    public void performList ( final Message message ) throws InvalidSessionException
    {
        ListMessage.fromMessage ( message );
        new ListController ( this.storage, this.session, this.messenger ).run ( message );
    }

    synchronized public void performSubscribe ( final Message message ) throws Exception
    {
        final SubscribeMessage subscribeMessage = SubscribeMessage.fromMessage ( message );
        log.debug ( String.format ( "Requested subscribe: Query: %s, LongRunningListener: %d, Batch Size: %d, Archive Set: %d", subscribeMessage.getQueryId (), subscribeMessage.getListenerId (), subscribeMessage.getMaxBatchSize (), subscribeMessage.getArchiveSet () ) );
        final EventListener eventListener = new EventListener ( subscribeMessage.getQueryId (), subscribeMessage.getListenerId (), this );
        try
        {
            this.listenerMap.put ( subscribeMessage.getListenerId (), eventListener );
            this.storage.subscribe ( this.session, subscribeMessage.getQueryId (), eventListener, subscribeMessage.getMaxBatchSize (), subscribeMessage.getArchiveSet () );
        }
        catch ( final Exception e )
        {
            this.listenerMap.remove ( subscribeMessage.getListenerId () );
            log.warn ( "Subscribe failed", e );
            throw e;
        }
    }

    synchronized public void performUnsubscribe ( final Message message ) throws InvalidSessionException, NoSuchQueryException
    {
        final UnsubscribeMessage unsubscribeMessage = UnsubscribeMessage.fromMessage ( message );
        final EventListener eventListener = this.listenerMap.get ( unsubscribeMessage.getListenerId () );
        if ( eventListener != null )
        {
            this.storage.unsubscribe ( this.session, unsubscribeMessage.getQueryId (), eventListener );
            this.listenerMap.remove ( unsubscribeMessage.getListenerId () );
        }
    }

    public void events ( final String queryId, final long listenerId, final EventInformation[] events )
    {
        log.debug ( "Got events for: " + queryId + "/" + listenerId );

        final EventMessage eventMessage = new EventMessage ();
        eventMessage.setQueryId ( queryId );
        eventMessage.setEvents ( Arrays.asList ( events ) );
        eventMessage.setListenerId ( listenerId );
        this.messenger.sendMessage ( eventMessage.toMessage () );
    }

    synchronized public void unsubscribed ( final String queryId, final long listenerId, final String reason )
    {
        this.listenerMap.remove ( listenerId );
        final UnsubscribedMessage message = new UnsubscribedMessage ();
        message.setQueryId ( queryId );
        message.setReason ( reason );
        message.setListenerId ( listenerId );
        this.messenger.sendMessage ( message.toMessage () );
    }

    public void performSubmitEvent ( final Message message )
    {
        final SubmitEventMessage submitEventMessage = SubmitEventMessage.fromMessage ( message );

        try
        {
            this.storage.submitEvent ( submitEventMessage.getProperties (), submitEventMessage.getEvent () );
            this.messenger.sendMessage ( MessageCreator.createACK ( message ) );
        }
        catch ( final Throwable e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
        }
    }
}
