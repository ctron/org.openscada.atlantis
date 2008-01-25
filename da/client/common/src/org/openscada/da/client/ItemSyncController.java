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

    private org.openscada.da.client.Connection _connection;

    private String _itemName;

    private boolean _subscribed = false;

    private Variant _cachedValue = new Variant ();
    private Map<String, Variant> _cachedAttributes = new HashMap<String, Variant> ();
    private SubscriptionState _subscriptionState = SubscriptionState.DISCONNECTED;
    private Throwable _subscriptionError = null;

    /**
     * Holds some additional listener information 
     * @author jens
     *
     */
    private class ListenerInfo
    {
        private ItemUpdateListener _listener;

        public ListenerInfo ( ItemUpdateListener listener )
        {
            _listener = listener;
        }

        public ItemUpdateListener getListener ()
        {
            return _listener;
        }

        @Override
        public boolean equals ( Object obj )
        {
            if ( obj == null )
                return false;
            if ( obj == this )
                return true;

            if ( obj instanceof ItemUpdateListener )
            {
                return obj == _listener;
            }
            else if ( obj instanceof ListenerInfo )
            {
                return ( (ListenerInfo)obj )._listener == _listener;
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode ()
        {
            return _listener.hashCode ();
        }
    }

    private Map<ItemUpdateListener, ListenerInfo> _listeners = new HashMap<ItemUpdateListener, ListenerInfo> ();

    public ItemSyncController ( org.openscada.da.client.Connection connection, String itemName )
    {
        _connection = connection;
        _itemName = itemName;

        _connection.setItemUpdateListener ( _itemName, this );
    }

    public String getItemName ()
    {
        return _itemName;
    }

    public synchronized int getNumberOfListeners ()
    {
        return _listeners.size ();
    }

    public synchronized void add ( ItemUpdateListener listener )
    {
        if ( !_listeners.containsKey ( listener ) )
        {
            _listeners.put ( listener, new ListenerInfo ( listener ) );
            listener.notifySubscriptionChange ( _subscriptionState, _subscriptionError );
            listener.notifyValueChange ( _cachedValue, true );
            listener.notifyAttributeChange ( _cachedAttributes, true );

            triggerSync ();
        }
    }

    public synchronized void remove ( ItemUpdateListener listener )
    {
        if ( _listeners.containsKey ( listener ) )
        {
            _listeners.remove ( listener );

            triggerSync ();
        }
    }

    public void triggerSync ()
    {
        Thread t = new Thread ( new Runnable () {

            public void run ()
            {
                sync ( false );
            }} );
        t.setDaemon ( true );
        t.setName ( "TriggerSync" );
        t.start ();
    }

    public synchronized void sync ( boolean force )
    {
        boolean subscribe = getNumberOfListeners () > 0;

        if ( ( _subscribed == subscribe ) && !force )
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
            _subscribed = true;
            _connection.subscribeItem ( _itemName );
        }
        catch ( Throwable e )
        {
            handleError ( e );
        }
    }

    protected void unsubscribe ()
    {
        try
        {
            _log.debug ( "Syncing listen state: inactive " );
            _subscribed = false;
            _connection.unsubscribeItem ( _itemName );
            notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        }
        catch ( Throwable e )
        {
            handleError ( e );
        }
    }

    private void handleError ( Throwable e )
    {
        _log.warn ( "Failed to subscribe", e );
        _subscribed = false;
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, e );
    }

    public synchronized void notifyValueChange ( Variant value, boolean cache )
    {
        if ( _cachedValue.equals ( value ) )
        {
            return;
        }
        
        _cachedValue = value;
        for ( ListenerInfo listenerInfo : _listeners.values () )
        {
            listenerInfo.getListener ().notifyValueChange ( value, cache );
        }
    }

    public synchronized void notifyAttributeChange ( Map<String, Variant> attributes, boolean full )
    {
        AttributesHelper.mergeAttributes ( _cachedAttributes, attributes, full );

        for ( ListenerInfo listenerInfo : _listeners.values () )
        {
            listenerInfo.getListener ().notifyAttributeChange ( attributes, full );
        }
    }

    public synchronized void notifySubscriptionChange ( SubscriptionState subscriptionState, Throwable e )
    {
        if ( _subscriptionState.equals ( subscriptionState ) && _subscriptionError == e )
        {
            return;
        }
        
        _subscriptionState = subscriptionState;
        _subscriptionError = e;

        for ( ListenerInfo listenerInfo : _listeners.values () )
        {
            listenerInfo.getListener ().notifySubscriptionChange ( subscriptionState, e );
        }
    }

    public synchronized void disconnect ()
    {
        notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
        notifyValueChange ( new Variant (), true );
        notifyAttributeChange ( new HashMap<String, Variant> (), true );
    }
}
