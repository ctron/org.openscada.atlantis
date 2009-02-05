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

package org.openscada.da.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;

/**
 * A controller that synchronizes the subscription state for one item.
 * <br>
 * @author Jens Reimann <jens.reimann@inavare.net>
 *
 */
public class ItemSyncController implements ItemUpdateListener
{
    private static Logger _log = Logger.getLogger ( ItemSyncController.class );

    private final org.openscada.da.client.Connection _connection;

    private final String _itemName;

    private boolean _subscribed = false;

    private Variant _cachedValue = new Variant ();

    private final Map<String, Variant> _cachedAttributes = new ConcurrentHashMap<String, Variant> ();

    private SubscriptionState _subscriptionState = SubscriptionState.DISCONNECTED;

    private Throwable _subscriptionError = null;

    /**
     * Holds some additional listener information 
     * @author jens
     *
     */
    private class ListenerInfo
    {
        private final ItemUpdateListener _listener;

        public ListenerInfo ( final ItemUpdateListener listener )
        {
            this._listener = listener;
        }

        public ItemUpdateListener getListener ()
        {
            return this._listener;
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
                return obj == this._listener;
            }
            else if ( obj instanceof ListenerInfo )
            {
                return ( (ListenerInfo)obj )._listener == this._listener;
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode ()
        {
            return this._listener.hashCode ();
        }
    }

    private final Map<ItemUpdateListener, ListenerInfo> _listeners = new HashMap<ItemUpdateListener, ListenerInfo> ();

    public ItemSyncController ( final org.openscada.da.client.Connection connection, final String itemName )
    {
        this._connection = connection;
        this._itemName = itemName;

        this._connection.setItemUpdateListener ( this._itemName, this );
    }

    public String getItemName ()
    {
        return this._itemName;
    }

    public synchronized void add ( final ItemUpdateListener listener )
    {
        if ( !this._listeners.containsKey ( listener ) )
        {
            this._listeners.put ( listener, new ListenerInfo ( listener ) );
            listener.notifySubscriptionChange ( this._subscriptionState, this._subscriptionError );
            listener.notifyDataChange ( this._cachedValue, this._cachedAttributes, true );

            triggerSync ();
        }
    }

    public synchronized void remove ( final ItemUpdateListener listener )
    {
        if ( this._listeners.containsKey ( listener ) )
        {
            this._listeners.remove ( listener );

            triggerSync ();
        }
    }

    public void triggerSync ()
    {
        final Thread t = new Thread ( new Runnable () {

            public void run ()
            {
                sync ( false );
            }
        } );
        t.setDaemon ( true );
        t.setName ( "TriggerSync" );
        t.start ();
    }

    public synchronized void sync ( final boolean force )
    {
        final boolean subscribe = !this._listeners.isEmpty();

        if ( this._subscribed == subscribe && !force )
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

    protected void subscribe ()
    {
        try
        {
            _log.debug ( "Syncing listen state: active" );
            this._subscribed = true;
            this._connection.subscribeItem ( this._itemName );
        }
        catch ( final Throwable e )
        {
            handleError ( e );
        }
    }

    protected void unsubscribe ()
    {
        try
        {
            _log.debug ( "Syncing listen state: inactive" );
            this._subscribed = false;
            this._connection.unsubscribeItem ( this._itemName );
            notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        }
        catch ( final Throwable e )
        {
            handleError ( e );
        }
    }

    private void handleError ( final Throwable e )
    {
        _log.warn ( "Failed to subscribe", e );
        this._subscribed = false;
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, e );
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        boolean change = false;

        synchronized ( this )
        {
            // update value
            if ( this._cachedValue == null || !this._cachedValue.equals ( value ) )
            {
                change = true;
                this._cachedValue = value;
            }

            // update attributes
            if ( attributes != null )
            {
                if ( !attributes.isEmpty () || cache )
                {
                    AttributesHelper.mergeAttributes ( this._cachedAttributes, attributes, cache );
                    change = true;
                }
            }
        }

        if ( change )
        {
            for ( final ListenerInfo listenerInfo : this._listeners.values () )
            {
                listenerInfo.getListener ().notifyDataChange ( value, attributes, cache );
            }
        }
    }

    public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable e )
    {
        synchronized ( this )
        {
            if ( this._subscriptionState.equals ( subscriptionState ) && this._subscriptionError == e )
            {
                return;
            }

            this._subscriptionState = subscriptionState;
            this._subscriptionError = e;
        }

        for ( final ListenerInfo listenerInfo : this._listeners.values () )
        {
            listenerInfo.getListener ().notifySubscriptionChange ( subscriptionState, e );
        }
    }

    public synchronized void disconnect ()
    {
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        notifyDataChange ( new Variant (), new HashMap<String, Variant> (), true );
    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalizing..." );
        super.finalize ();
    }
}
