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
import java.util.Observable;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;

public class DataItem extends Observable
{
    private String _itemId;

    private ItemManager _itemManager = null;

    private Variant _value = new Variant ();
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private SubscriptionState _subscriptionState = SubscriptionState.DISCONNECTED;
    private Throwable _subscriptionError = null;

    private ItemUpdateListener _listener = null;

    /**
     * create a new data item structure.
     * <p>
     * Note that the item is initially unconnected
     * @param itemId the id of the item to register later
     */
    public DataItem ( String itemId )
    {
        _itemId = itemId;
    }

    /**
     * create a new data item structure.
     * <p>
     * Note that the item is initially connected to the item manager provided. You must call {@link #unregister()}
     * when you want to clear the subscription with the item on the server.
     * @param itemId the id of the item to register
     * @param connection the item manager to which the item will register
     */
    public DataItem ( String itemId, ItemManager connection )
    {
        this ( itemId );

        register ( connection );
    }

    synchronized public void register ( ItemManager connection )
    {
        if ( _itemManager == connection )
        {
            return;
        }
        unregister ();

        _listener = new ItemUpdateListener () {

            public void notifyDataChange ( Variant value, Map<String, Variant> attributes, boolean cache )
            {
                performNotifyDataChange ( value, attributes, cache );
            }

            public void notifySubscriptionChange ( SubscriptionState subscriptionState, Throwable subscriptionError )
            {
                performNotifySubscriptionChange ( subscriptionState, subscriptionError );
            }
        };

        _itemManager = connection;
        _itemManager.addItemUpdateListener ( _itemId, _listener );
    }

    public void unregister ()
    {
        ItemManager manager;
        ItemUpdateListener listener;
        
        synchronized ( this )
        {
            if ( _itemManager == null )
            {
                return;
            }
            manager = _itemManager;
            listener = _listener;
            
            _itemManager = null;
            _listener = null;
        }

        manager.removeItemUpdateListener ( _itemId, listener );
    }

    private void performNotifyDataChange ( Variant value, Map<String, Variant> attributes, boolean cache )
    {
        if ( cache )
        {
            setChanged ();
            _attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            AttributesHelper.mergeAttributes ( _attributes, attributes );
        }
        
        if ( attributes != null )
        {
            setChanged ();
        }
        
        if ( value != null )
        {
            setChanged ();
            _value = new Variant ( value );
        }
        
        notifyObservers ();
    }
    
    private void performNotifySubscriptionChange ( SubscriptionState subscriptionState, Throwable subscriptionError )
    {
        _subscriptionState = subscriptionState;
        _subscriptionError = subscriptionError;

        setChanged ();
        notifyObservers ();
    }

    /**
     * Fetch the current cached value.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current value
     */
    public Variant getValue ()
    {
        return _value;
    }

    /**
     * Fetch the current cached attributes.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current attributes
     */
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    /**
     * Get the subscription state
     * @return the subscription state
     */
    public SubscriptionState getSubscriptionState ()
    {
        return _subscriptionState;
    }

    /**
     * Get the item ID
     * @return the item Id
     */
    public String getItemId ()
    {
        return _itemId;
    }

    /**
     * Get the subscription error or <code>null</code> if there was none
     * @return the subscription error
     */
    public Throwable getSubscriptionError ()
    {
        return _subscriptionError;
    }

}
