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

package org.openscada.ae.client.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.ae.client.net.operations.ListOperationController;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.net.CreateSessionMessage;
import org.openscada.ae.net.EventMessage;
import org.openscada.ae.net.ListReplyMessage;
import org.openscada.ae.net.Messages;
import org.openscada.ae.net.SubscribeMessage;
import org.openscada.ae.net.UnsubscribeMessage;
import org.openscada.ae.net.UnsubscribedMessage;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.DisconnectReason;
import org.openscada.core.client.net.OperationTimedOutException;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.IOProcessor;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class Connection extends ConnectionBase implements org.openscada.ae.client.Connection
{
    public static final String VERSION = "0.1.0";

    public static final String PROP_RECONNECT_DELAY = "reconnect-delay";

    public static final String PROP_AUTO_RECONNECT = "auto-reconnect";

    private static Logger logger = Logger.getLogger ( Connection.class );

    private ListOperationController listOperationController = null;

    private final Map<Long, org.openscada.ae.core.Listener> eventListenerMap = new HashMap<Long, org.openscada.ae.core.Listener> ();

    public Connection ( final ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );

        init ();
    }

    public Connection ( final ConnectionInfo connectionInfo, final IOProcessor processor )
    {
        super ( processor, connectionInfo );

        init ();
    }

    private void init ()
    {
        this.listOperationController = new ListOperationController ( this.client );
        this.listOperationController.register ( this.client.getMessageProcessor () );

        this.client.getMessageProcessor ().setHandler ( Messages.CC_SUBSCRIPTION_EVENT, new MessageListener () {

            public void messageReceived ( final org.openscada.net.io.net.Connection connection, final Message message ) throws Exception
            {
                handleSubscriptionEvent ( message );
            }
        } );

        this.client.getMessageProcessor ().setHandler ( Messages.CC_SUBSCRIPTION_UNSUBSCRIBED, new MessageListener () {

            public void messageReceived ( final org.openscada.net.io.net.Connection connection, final Message message ) throws Exception
            {
                handleSubscriptionUnsubscribed ( message );
            }
        } );
    }

    private void requestSession ()
    {
        if ( this.client == null )
        {
            return;
        }

        final Properties props = new Properties ();
        props.setProperty ( "client-version", VERSION );

        final CreateSessionMessage message = new CreateSessionMessage ();
        message.setProperties ( props );
        this.client.getConnection ().sendMessage ( message.toMessage (), new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                disconnect ( new OperationTimedOutException ().fillInStackTrace () );
            }
        }, 10 * 1000 );
    }

    private void processSessionReply ( final Message message )
    {
        logger.debug ( "Got session reply!" );

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

    public LongRunningOperation startList ( final LongRunningListener listener )
    {
        return this.listOperationController.start ( listener );
    }

    public Set<QueryDescription> completeList ( final LongRunningOperation operation ) throws OperationException
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
                final ListReplyMessage listReplyMessage = ListReplyMessage.fromMessage ( reply );
                return listReplyMessage.getQueries ();
            }
            catch ( final Exception e )
            {
                throw new OperationException ( e );
            }
        }
        return null;
    }

    public Set<QueryDescription> list () throws InterruptedException, OperationException
    {
        final LongRunningOperation op = startList ( null );
        op.waitForCompletion ();
        return completeList ( op );
    }

    synchronized public void subscribe ( final String queryId, final org.openscada.ae.core.Listener listener, final int maxBatchSize, final int archiveSet )
    {
        final Random r = new Random ();
        Long id;

        do
        {
            id = r.nextLong ();
        } while ( this.eventListenerMap.containsKey ( id ) );

        this.eventListenerMap.put ( id, listener );

        final SubscribeMessage message = new SubscribeMessage ();
        message.setQueryId ( queryId );
        message.setListenerId ( id );
        message.setArchiveSet ( archiveSet );
        message.setMaxBatchSize ( maxBatchSize );
        getClient ().getConnection ().sendMessage ( message.toMessage () );
    }

    synchronized public void unsubscribe ( final String queryId, final org.openscada.ae.core.Listener listener )
    {
        final Long id = findListenerId ( listener );

        if ( id == null )
        {
            return;
        }

        final UnsubscribeMessage message = new UnsubscribeMessage ();
        message.setListenerId ( id );
        message.setQueryId ( queryId );
        getClient ().getConnection ().sendMessage ( message.toMessage () );
    }

    synchronized private Long findListenerId ( final org.openscada.ae.core.Listener listener )
    {
        for ( final Map.Entry<Long, org.openscada.ae.core.Listener> entry : this.eventListenerMap.entrySet () )
        {
            if ( entry.getValue () == listener )
            {
                return entry.getKey ();
            }
        }
        return null;
    }

    synchronized private void handleSubscriptionEvent ( final Message message )
    {
        final EventMessage eventMessage = EventMessage.fromMessage ( message );

        final org.openscada.ae.core.Listener listener = this.eventListenerMap.get ( eventMessage.getListenerId () );

        if ( listener != null )
        {
            listener.events ( eventMessage.getEvents ().toArray ( new EventInformation[eventMessage.getEvents ().size ()] ) );
        }
    }

    private void handleSubscriptionUnsubscribed ( final Message message )
    {
        final UnsubscribedMessage unsubscribedMessage = UnsubscribedMessage.fromMessage ( message );
        final org.openscada.ae.core.Listener listener = this.eventListenerMap.get ( unsubscribedMessage.getListenerId () );

        if ( listener != null )
        {
            listener.unsubscribed ( unsubscribedMessage.getReason () );
            this.eventListenerMap.remove ( unsubscribedMessage.getListenerId () );
        }
    }

    public ConnectionInformation getConnectionInformation ()
    {
        final ConnectionInformation info = new ConnectionInformation ();
        info.setInterface ( "ae" );
        info.setDriver ( "net" );
        info.setTarget ( this.connectionInfo.getHostName () );
        info.setSecondaryTarget ( this.connectionInfo.getPort () );

        final Map<String, String> properties = new HashMap<String, String> ();
        if ( this.connectionInfo.getReconnectDelay () > 0 )
        {
            properties.put ( PROP_AUTO_RECONNECT, "true" );
            properties.put ( PROP_RECONNECT_DELAY, String.format ( "%s", this.connectionInfo.getReconnectDelay () ) );
        }

        info.setProperties ( properties );

        return info;
    }
}
