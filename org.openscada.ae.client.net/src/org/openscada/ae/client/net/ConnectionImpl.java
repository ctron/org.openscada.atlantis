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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.mina.core.session.IoSession;
import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.QueryListener;
import org.openscada.ae.client.ConditionListener;
import org.openscada.ae.client.EventListener;
import org.openscada.ae.net.BrowserMessageHelper;
import org.openscada.ae.net.ConditionMessageHelper;
import org.openscada.ae.net.EventMessageHelper;
import org.openscada.ae.net.Messages;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.net.SessionConnectionBase;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl extends SessionConnectionBase implements org.openscada.ae.client.Connection
{

    private static final String MESSAGE_QUERY_ID = "queryId";

    static
    {
        DriverFactoryImpl.registerDriver ();
    }

    public static final String VERSION = "0.1.0";

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionImpl.class );

    private Executor executor;

    private final Map<String, ConditionListener> conditionListeners = new HashMap<String, ConditionListener> ();

    private final Map<String, EventListener> eventListeners = new HashMap<String, EventListener> ();

    private final Set<BrowserListener> browserListeners = new CopyOnWriteArraySet<BrowserListener> ();

    private final Map<String, BrowserEntry> browserCache = new HashMap<String, BrowserEntry> ();

    @Override
    public String getRequiredVersion ()
    {
        return VERSION;
    }

    public ConnectionImpl ( final ConnectionInformation connectionInformantion )
    {
        super ( connectionInformantion );

        setupAsyncExecutor ();
        init ();
    }

    private void setupAsyncExecutor ()
    {
        this.executor = Executors.newSingleThreadExecutor ( new ThreadFactory () {

            public Thread newThread ( final Runnable r )
            {
                final Thread t = new Thread ( r, "ConnectionExecutor/" + getConnectionInformation () );
                t.setDaemon ( true );
                return t;
            }
        } );
    }

    protected void init ()
    {
        this.messenger.setHandler ( Messages.CC_CONDITIONS_STATUS, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleConditionStatus ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_CONDITIONS_DATA, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleConditionData ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_EVENT_POOL_STATUS, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleEventStatus ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_EVENT_POOL_DATA, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleEventData ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_UPDATE, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleBrowserUpdate ( message );
            }
        } );
    }

    protected void handleBrowserUpdate ( final Message message )
    {
        final BrowserEntry[] added = BrowserMessageHelper.fromValue ( message.getValues ().get ( "added" ) );
        final String[] removed = BrowserMessageHelper.fromValueRemoved ( message.getValues ().get ( "removed" ) );

        // perform update
        if ( removed != null )
        {
            for ( final String id : removed )
            {
                this.browserCache.remove ( id );
            }
        }
        if ( added != null )
        {
            for ( final BrowserEntry entry : added )
            {
                this.browserCache.put ( entry.getId (), entry );
            }
        }
        fireBrowserListener ( added, removed );
    }

    protected void handleEventData ( final Message message )
    {
        String queryId = null;
        {
            final Value value = message.getValues ().get ( MESSAGE_QUERY_ID );
            if ( value instanceof StringValue )
            {
                queryId = ( (StringValue)value ).getValue ();
            }
        }

        final Event[] data = EventMessageHelper.fromValue ( message.getValues ().get ( "events" ) );

        if ( queryId != null && data != null )
        {
            EventListener listener;
            synchronized ( this.eventListeners )
            {
                listener = this.eventListeners.get ( queryId );
            }
            fireEventDataChange ( listener, data );
        }
    }

    private void fireEventDataChange ( final EventListener listener, final Event[] data )
    {
        if ( listener == null )
        {
            return;
        }

        try
        {
            listener.dataChanged ( data );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify", e );
        }
    }

    protected void handleConditionData ( final Message message )
    {
        try
        {
            logger.debug ( "Got condition data" );

            String queryId = null;
            {
                final Value value = message.getValues ().get ( MESSAGE_QUERY_ID );
                if ( value instanceof StringValue )
                {
                    queryId = ( (StringValue)value ).getValue ();
                }
            }

            final ConditionStatusInformation[] data = ConditionMessageHelper.fromValue ( message.getValues ().get ( "conditions.addedOrUpdated" ) );
            final String[] removed = ConditionMessageHelper.fromValueRemoved ( message.getValues ().get ( "conditions.removed" ) );

            if ( queryId != null && ( data != null || removed != null ) )
            {
                ConditionListener listener;
                synchronized ( this.conditionListeners )
                {
                    listener = this.conditionListeners.get ( queryId );
                }
                fireConditionDataChange ( listener, data, removed );
            }
            else
            {
                logger.info ( "Nothing to notify" );
            }
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to handle condition data", e );
        }

    }

    private void fireConditionDataChange ( final ConditionListener listener, final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( listener == null )
        {
            logger.warn ( "Condition change data without a listener" );
            return;
        }

        try
        {
            logger.debug ( "notify condition data change" );
            listener.dataChanged ( addedOrUpdated, removed );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify", e );
        }
    }

    protected void handleEventStatus ( final Message message )
    {
        String queryId = null;
        {
            final Value value = message.getValues ().get ( MESSAGE_QUERY_ID );
            if ( value instanceof StringValue )
            {
                queryId = ( (StringValue)value ).getValue ();
            }
        }

        SubscriptionState status = null;
        {
            final Value value = message.getValues ().get ( "status" );
            if ( value instanceof StringValue )
            {
                final String statusString = ( (StringValue)value ).getValue ();
                status = SubscriptionState.valueOf ( statusString );
            }
        }

        if ( queryId != null && status != null )
        {
            EventListener listener;
            synchronized ( this.eventListeners )
            {
                listener = this.eventListeners.get ( queryId );
            }
            fireEventStatusChange ( listener, status );
        }
    }

    protected void handleConditionStatus ( final Message message )
    {
        String queryId = null;
        {
            final Value value = message.getValues ().get ( MESSAGE_QUERY_ID );
            if ( value instanceof StringValue )
            {
                queryId = ( (StringValue)value ).getValue ();
            }
        }

        SubscriptionState status = null;
        {
            final Value value = message.getValues ().get ( "status" );
            if ( value instanceof StringValue )
            {
                final String statusString = ( (StringValue)value ).getValue ();
                status = SubscriptionState.valueOf ( statusString );
            }
        }

        if ( queryId != null && status != null )
        {
            ConditionListener listener;
            synchronized ( this.conditionListeners )
            {
                listener = this.conditionListeners.get ( queryId );
            }
            fireConditionStatusChange ( listener, status );
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

    public void createQuery ( final String queryType, final String queryData, final QueryListener listener )
    {
        throw new RuntimeException ( "Not implemented" );
    }

    public void setConditionListener ( final String conditionQueryId, final ConditionListener listener )
    {
        if ( listener == null )
        {
            clearConditionListener ( conditionQueryId );
        }
        else
        {
            updateConditionListener ( conditionQueryId, listener );
        }
    }

    private void updateConditionListener ( final String conditionQueryId, final ConditionListener listener )
    {
        ConditionListener oldListener;

        synchronized ( this.conditionListeners )
        {
            oldListener = this.conditionListeners.put ( conditionQueryId, listener );
            if ( oldListener == listener )
            {
                return;
            }

            if ( oldListener == null )
            {
                sendSubscribeConditions ( conditionQueryId, true );
            }
        }

        // initially send DISCONNECTED
        fireConditionStatusChange ( listener, SubscriptionState.DISCONNECTED );

        if ( oldListener != null )
        {
            fireConditionStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
    }

    private void clearConditionListener ( final String conditionQueryId )
    {
        ConditionListener oldListener;

        synchronized ( this.conditionListeners )
        {
            oldListener = this.conditionListeners.remove ( conditionQueryId );
            if ( oldListener != null )
            {
                sendSubscribeConditions ( conditionQueryId, false );
            }
        }
        if ( oldListener != null )
        {
            fireConditionStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
    }

    /**
     * Send a message to request (un)subscription
     * @param conditionQueryId the condition query id
     * @param flag <code>true</code> for subscription, <code>false</code> otherwise
     */
    private void sendSubscribeConditions ( final String conditionQueryId, final boolean flag )
    {
        logger.info ( "Requesting conditions: " + conditionQueryId + "/" + flag );

        final Message message = new Message ( flag ? Messages.CC_SUBSCRIBE_CONDITIONS : Messages.CC_UNSUBSCRIBE_CONDITIONS );

        message.getValues ().put ( MESSAGE_QUERY_ID, new StringValue ( conditionQueryId ) );

        this.messenger.sendMessage ( message );
    }

    private void fireConditionStatusChange ( final ConditionListener listener, final SubscriptionState status )
    {
        if ( listener == null )
        {
            return;
        }

        try
        {
            listener.statusChanged ( status );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify", e );
        }
    }

    public void setEventListener ( final String eventQueryId, final EventListener listener )
    {
        if ( listener == null )
        {
            clearEventListener ( eventQueryId );
        }
        else
        {
            updateEventListener ( eventQueryId, listener );
        }
    }

    private void updateEventListener ( final String eventQueryId, final EventListener listener )
    {
        EventListener oldListener;

        synchronized ( this.eventListeners )
        {
            oldListener = this.eventListeners.put ( eventQueryId, listener );
            if ( oldListener == listener )
            {
                return;
            }

            if ( oldListener == null )
            {
                sendSubscribeEventQuery ( eventQueryId, true );
            }
        }

        // initially send DISCONNECTED
        fireEventStatusChange ( listener, SubscriptionState.DISCONNECTED );

        if ( oldListener != null )
        {
            fireEventStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
    }

    private void clearEventListener ( final String eventQueryId )
    {
        EventListener oldListener;

        synchronized ( this.eventListeners )
        {
            oldListener = this.eventListeners.remove ( eventQueryId );
            if ( oldListener != null )
            {
                sendSubscribeConditions ( eventQueryId, false );
            }
        }
        if ( oldListener != null )
        {
            fireEventStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
    }

    private void fireEventStatusChange ( final EventListener listener, final SubscriptionState status )
    {
        if ( listener == null )
        {
            return;
        }

        try
        {
            listener.statusChanged ( status );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify", e );
        }
    }

    /**
     * Send a message to request (un)subscription
     * @param eventQueryId the event query id
     * @param flag <code>true</code> for subscription, <code>false</code> otherwise
     */
    private void sendSubscribeEventQuery ( final String eventQueryId, final boolean flag )
    {
        final Message message = new Message ( flag ? Messages.CC_SUBSCRIBE_EVENT_POOL : Messages.CC_UNSUBSCRIBE_EVENT_POOL );

        message.getValues ().put ( MESSAGE_QUERY_ID, new StringValue ( eventQueryId ) );

        this.messenger.sendMessage ( message );
    }

    @Override
    public void sessionClosed ( final IoSession session ) throws Exception
    {
        // set states to DISCONNECTED
        for ( final ConditionListener listener : this.conditionListeners.values () )
        {
            fireConditionStatusChange ( listener, SubscriptionState.DISCONNECTED );
        }
        for ( final EventListener listener : this.eventListeners.values () )
        {
            fireEventStatusChange ( listener, SubscriptionState.DISCONNECTED );
        }

        fireBrowserListener ( null, this.browserCache.keySet ().toArray ( new String[0] ) );
        this.browserCache.clear ();

        super.sessionClosed ( session );
    }

    public void addBrowserListener ( final BrowserListener listener )
    {
        if ( this.browserListeners.add ( listener ) )
        {
            listener.dataChanged ( this.browserCache.values ().toArray ( new BrowserEntry[0] ), null );
        }
    }

    public void removeBrowserListener ( final BrowserListener listener )
    {
        this.browserListeners.remove ( listener );
    }

    protected void fireBrowserListener ( final BrowserEntry[] added, final String[] removed )
    {
        for ( final BrowserListener listener : this.browserListeners )
        {
            try
            {
                listener.dataChanged ( added, removed );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to notify browser change", e );
            }
        }
    }

    public void acknowledge ( final String conditionId, final Date aknTimestamp )
    {
        final Message message = new Message ( Messages.CC_CONDITION_AKN );
        message.getValues ().put ( "id", new StringValue ( conditionId ) );
        message.getValues ().put ( "aknTimestamp", new LongValue ( aknTimestamp.getTime () ) );
        this.messenger.sendMessage ( message );
    }
}
