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

package org.openscada.da.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A controller that synchronizes the subscription state for one item.
 * @author Jens Reimann <jens.reimann@inavare.net>
 *
 */
public class ItemSyncController implements ItemUpdateListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemSyncController.class );

    private final org.openscada.da.client.Connection connection;

    private final String itemId;

    private boolean subscribed = false;

    private Variant cachedValue = new Variant ();

    private final Map<String, Variant> cachedAttributes = new ConcurrentHashMap<String, Variant> ();

    private SubscriptionState subscriptionState = SubscriptionState.DISCONNECTED;

    private Throwable subscriptionError = null;

    /**
     * Holds some additional listener information 
     * @author jens
     *
     */
    private static class ListenerInfo
    {
        private final ItemUpdateListener listener;

        public ListenerInfo ( final ItemUpdateListener listener )
        {
            this.listener = listener;
        }

        public ItemUpdateListener getListener ()
        {
            return this.listener;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( obj == null )
            {
                return false;
            }
            if ( obj == this )
            {
                return true;
            }

            if ( obj instanceof ItemUpdateListener )
            {
                return obj == this.listener;
            }
            else if ( obj instanceof ListenerInfo )
            {
                return ( (ListenerInfo)obj ).listener == this.listener;
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode ()
        {
            return this.listener.hashCode ();
        }
    }

    private final Map<ItemUpdateListener, ListenerInfo> listeners = new ConcurrentHashMap<ItemUpdateListener, ListenerInfo> ();

    private final ItemManager itemManager;

    public ItemSyncController ( final org.openscada.da.client.Connection connection, final ItemManager itemManager, final String itemId )
    {
        this.connection = connection;
        this.itemManager = itemManager;
        this.itemId = itemId;

        synchronized ( this )
        {
            this.connection.setItemUpdateListener ( this.itemId, this );
        }
    }

    public String getItemName ()
    {
        return this.itemId;
    }

    public synchronized void add ( final ItemUpdateListener listener )
    {
        if ( !this.listeners.containsKey ( listener ) )
        {
            this.listeners.put ( listener, new ListenerInfo ( listener ) );

            final SubscriptionState state = this.subscriptionState;
            final Throwable error = this.subscriptionError;
            final Variant value = this.cachedValue;
            final Map<String, Variant> attributes = new HashMap<String, Variant> ( this.cachedAttributes );

            // send the initial update
            this.itemManager.getExecutor ().execute ( new Runnable () {

                public void run ()
                {
                    listener.notifySubscriptionChange ( state, error );
                    listener.notifyDataChange ( value, attributes, true );
                }
            } );

            triggerSync ();
        }
    }

    public synchronized void remove ( final ItemUpdateListener listener )
    {
        if ( this.listeners.containsKey ( listener ) )
        {
            this.listeners.remove ( listener );

            triggerSync ();
        }
    }

    public synchronized void triggerSync ()
    {
        this.itemManager.getExecutor ().execute ( new Runnable () {

            public void run ()
            {
                sync ( false );
            }
        } );
    }

    public synchronized void sync ( final boolean force )
    {
        final boolean subscribe = !this.listeners.isEmpty ();

        if ( this.subscribed == subscribe && !force )
        {
            // nothing to do
            return;
        }

        if ( subscribe )
        {
            subscribe ();
        }
        else
        {
            unsubscribe ();
        }
    }

    protected synchronized void subscribe ()
    {
        try
        {
            logger.debug ( "Syncing listen state: active" );
            this.subscribed = true;
            this.connection.subscribeItem ( this.itemId );
        }
        catch ( final Throwable e )
        {
            handleError ( e );
        }
    }

    protected synchronized void unsubscribe ()
    {
        try
        {
            logger.debug ( "Syncing listen state: inactive" );
            this.subscribed = false;
            this.connection.unsubscribeItem ( this.itemId );
            notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        }
        catch ( final Throwable e )
        {
            handleError ( e );
        }
    }

    private synchronized void handleError ( final Throwable e )
    {
        logger.warn ( "Failed to subscribe", e );
        this.subscribed = false;
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, e );
    }

    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        boolean change = false;

        // update value
        if ( this.cachedValue == null || !this.cachedValue.equals ( value ) )
        {
            change = true;
            this.cachedValue = value;
        }

        // update attributes
        if ( attributes != null )
        {
            if ( !attributes.isEmpty () || cache )
            {
                AttributesHelper.mergeAttributes ( this.cachedAttributes, attributes, cache );
                change = true;
            }
        }

        if ( change )
        {
            this.itemManager.getExecutor ().execute ( new Runnable () {

                public void run ()
                {
                    for ( final ListenerInfo listenerInfo : ItemSyncController.this.listeners.values () )
                    {
                        listenerInfo.getListener ().notifyDataChange ( value, attributes, cache );
                    }
                }
            } );
        }
    }

    public synchronized void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable e )
    {
        if ( this.subscriptionState.equals ( subscriptionState ) && this.subscriptionError == e )
        {
            return;
        }

        this.subscriptionState = subscriptionState;
        this.subscriptionError = e;

        this.itemManager.getExecutor ().execute ( new Runnable () {

            public void run ()
            {
                for ( final ListenerInfo listenerInfo : ItemSyncController.this.listeners.values () )
                {
                    listenerInfo.getListener ().notifySubscriptionChange ( subscriptionState, e );
                }
            }
        } );

    }

    public synchronized void disconnect ()
    {
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        notifyDataChange ( new Variant (), new HashMap<String, Variant> (), true );
    }

}
