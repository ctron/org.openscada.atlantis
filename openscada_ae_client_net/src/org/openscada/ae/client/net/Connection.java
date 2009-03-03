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
import java.util.Random;
import java.util.Set;

import org.openscada.ae.client.net.operations.ListOperationController;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.net.EventMessage;
import org.openscada.ae.net.ListReplyMessage;
import org.openscada.ae.net.Messages;
import org.openscada.ae.net.SubscribeMessage;
import org.openscada.ae.net.UnsubscribeMessage;
import org.openscada.ae.net.UnsubscribedMessage;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.net.SessionConnectionBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class Connection extends SessionConnectionBase implements org.openscada.ae.client.Connection
{
    public static final String VERSION = "0.1.0";

    public static final String PROP_RECONNECT_DELAY = "reconnect-delay";

    public static final String PROP_AUTO_RECONNECT = "auto-reconnect";

    private ListOperationController listOperationController = null;

    private final Map<Long, org.openscada.ae.core.Listener> eventListenerMap = new HashMap<Long, org.openscada.ae.core.Listener> ();

    public Connection ( final ConnectionInformation connectionInformation )
    {
        super ( connectionInformation );

        init ();
    }

    private void init ()
    {
        this.listOperationController = new ListOperationController ( this.messenger );
        this.listOperationController.register ();

        this.messenger.setHandler ( Messages.CC_SUBSCRIPTION_EVENT, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                handleSubscriptionEvent ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_SUBSCRIPTION_UNSUBSCRIBED, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                handleSubscriptionUnsubscribed ( message );
            }
        } );
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
        this.messenger.sendMessage ( message.toMessage () );
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
        this.messenger.sendMessage ( message.toMessage () );
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

    @Override
    public String getRequiredVersion ()
    {
        return VERSION;
    }
}
