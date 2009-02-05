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

package org.openscada.ae.storage.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
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
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.io.net.Connection;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.timing.Scheduler;

public class ServerConnectionHandler extends ConnectionHandlerBase implements QueryListener
{
    
    public final static String VERSION = "0.1.0";

    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( ServerConnectionHandler.class );

    private Storage _storage = null;
    private Session _session = null;
    
    private Map<Long, EventListener> _listenerMap = new HashMap<Long,EventListener> ();
 
    public ServerConnectionHandler ( Scheduler scheduler, Storage storage )
    {
        super( scheduler );

        _storage = storage;

        getMessageProcessor ().setHandler ( Messages.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                createSession ( message );
            }});

        getMessageProcessor ().setHandler ( Messages.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                closeSession ();
            }});
        
        getMessageProcessor ().setHandler ( Messages.CC_LIST, new MessageListener () {

            public void messageReceived ( Connection connection, Message message ) throws Exception
            {
                performList ( message );
            }});
        
        getMessageProcessor ().setHandler ( Messages.CC_SUBSCRIBE, new MessageListener () {

            public void messageReceived ( Connection connection, Message message ) throws Exception
            {
                performSubscribe ( message );
            }});
        
        getMessageProcessor ().setHandler ( Messages.CC_UNSUBSCRIBE, new MessageListener () {

            public void messageReceived ( Connection connection, Message message ) throws Exception
            {
                performUnsubscribe ( message );
            }});
        
        getMessageProcessor ().setHandler ( Messages.CC_SUBMIT_EVENT, new MessageListener () {

            public void messageReceived ( Connection connection, Message message ) throws Exception
            {
                performSubmitEvent ( message );
            }});
        
    }

    private void createSession ( Message message )
    {
        // if session exists this is an error
        if ( _session != null )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        Properties props = new Properties();
        for ( Map.Entry<String,Value> entry : message.getValues ().getValues ().entrySet() )
        {
            props.put ( entry.getKey(), entry.getValue().toString() );
        }
        
        // now check client version
        String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion + "' server: '" + VERSION + "'" ) );
            return;
        }

        try
        {
            _session = _storage.createSession ( props );
        }
        catch ( UnableToCreateSessionException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, e.getReason () ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( _session == null )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // send success
        getConnection ().sendMessage ( MessageCreator.createACK ( message ) );
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( _session != null )
        {
            try
            {
                _storage.closeSession ( _session );
            }
            catch (InvalidSessionException e)
            {
                e.printStackTrace ();
            }
        }	
    }

    private void closeSession ()
    {
        disposeSession ();
        // also shut down communcation connection
        getConnection().close();
    }

    private void cleanUp ()
    {
        disposeSession();
    }

    @Override
    public void closed ( Exception error )
    {
        cleanUp ();
        super.closed ( error );
    }

    public void performList ( Message message ) throws InvalidSessionException
    {
        ListMessage.fromMessage ( message );
        new ListController ( _storage, _session, this ).run ( message );
    }
    
    synchronized public void performSubscribe ( Message message ) throws Exception
    {
        SubscribeMessage subscribeMessage = SubscribeMessage.fromMessage ( message );
        _log.debug ( String.format ( "Requested subscribe: Query: %s, LongRunningListener: %d, Batch Size: %d, Archive Set: %d", subscribeMessage.getQueryId (), subscribeMessage.getListenerId (), subscribeMessage.getMaxBatchSize (), subscribeMessage.getArchiveSet () ) );
        EventListener eventListener = new EventListener ( subscribeMessage.getQueryId (), subscribeMessage.getListenerId (), this );
        try
        {
            _listenerMap.put ( subscribeMessage.getListenerId (), eventListener );
            _storage.subscribe ( _session, subscribeMessage.getQueryId (), eventListener, subscribeMessage.getMaxBatchSize (), subscribeMessage.getArchiveSet () );
        }
        catch ( Exception e )
        {
            _listenerMap.remove ( subscribeMessage.getListenerId () );
            _log.warn ( "Subscribe failed", e );
            throw e;
        }
    }
    
    synchronized public void performUnsubscribe ( Message message ) throws InvalidSessionException, NoSuchQueryException
    {
        UnsubscribeMessage unsubscribeMessage = UnsubscribeMessage.fromMessage ( message );
        EventListener eventListener = _listenerMap.get ( unsubscribeMessage.getListenerId () );
        if ( eventListener != null )
        {
            _storage.unsubscribe ( _session, unsubscribeMessage.getQueryId (), eventListener );
            _listenerMap.remove ( unsubscribeMessage.getListenerId ());
        }
    }

    public void events ( String queryId, long listenerId, EventInformation[] events )
    {
        _log.debug ( "Got events for: " + queryId + "/" + listenerId );
        
        EventMessage eventMessage = new EventMessage ();
        eventMessage.setQueryId ( queryId );
        eventMessage.setEvents ( Arrays.asList ( events ) );
        eventMessage.setListenerId ( listenerId );
        getConnection ().sendMessage ( eventMessage.toMessage () );
    }

    synchronized public void unsubscribed ( String queryId, long listenerId, String reason )
    {
        _listenerMap.remove ( listenerId );
        UnsubscribedMessage message = new UnsubscribedMessage ();
        message.setQueryId ( queryId );
        message.setReason ( reason );
        message.setListenerId ( listenerId );
        getConnection ().sendMessage ( message.toMessage () );
    }
    
    public void performSubmitEvent ( Message message )
    {
        SubmitEventMessage submitEventMessage = SubmitEventMessage.fromMessage ( message );
        
        try
        {
            _storage.submitEvent ( submitEventMessage.getProperties (), submitEventMessage.getEvent () );
            getConnection ().sendMessage ( MessageCreator.createACK ( message ) );
        }
        catch ( Throwable e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
        }
    }
}
