/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
    private final String _itemId;

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
    public DataItem ( final String itemId )
    {
        this._itemId = itemId;
    }

    /**
     * create a new data item structure.
     * <p>
     * Note that the item is initially connected to the item manager provided. You must call {@link #unregister()}
     * when you want to clear the subscription with the item on the server.
     * @param itemId the id of the item to register
     * @param connection the item manager to which the item will register
     */
    public DataItem ( final String itemId, final ItemManager connection )
    {
        this ( itemId );

        register ( connection );
    }

    synchronized public void register ( final ItemManager connection )
    {
        if ( this._itemManager == connection )
        {
            return;
        }
        unregister ();

        this._listener = new ItemUpdateListener () {

            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
                performNotifyDataChange ( value, attributes, cache );
            }

            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
                performNotifySubscriptionChange ( subscriptionState, subscriptionError );
            }
        };

        this._itemManager = connection;
        this._itemManager.addItemUpdateListener ( this._itemId, this._listener );
    }

    public void unregister ()
    {
        ItemManager manager;
        ItemUpdateListener listener;

        synchronized ( this )
        {
            if ( this._itemManager == null )
            {
                return;
            }
            manager = this._itemManager;
            listener = this._listener;

            this._itemManager = null;
            this._listener = null;
        }

        manager.removeItemUpdateListener ( this._itemId, listener );
    }

    private void performNotifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        synchronized ( this )
        {
            if ( cache )
            {
                setChanged ();
                this._attributes = new HashMap<String, Variant> ( attributes );
            }
            else
            {
                AttributesHelper.mergeAttributes ( this._attributes, attributes );
            }

            if ( attributes != null )
            {
                setChanged ();
            }

            if ( value != null )
            {
                setChanged ();
                this._value = new Variant ( value );
            }
        }

        notifyObservers ();
    }

    private void performNotifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        synchronized ( this )
        {
            this._subscriptionState = subscriptionState;
            this._subscriptionError = subscriptionError;
        }

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
        return this._value;
    }

    /**
     * Get the complete state of the current data item in an atomic operation
     * @return the current state of the data item 
     */
    public synchronized DataItemValue getSnapshotValue ()
    {
        return new DataItemValue ( this._value, this._attributes, this._subscriptionState );
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
        return this._attributes;
    }

    /**
     * Get the subscription state
     * @return the subscription state
     */
    public SubscriptionState getSubscriptionState ()
    {
        return this._subscriptionState;
    }

    /**
     * Get the item ID
     * @return the item Id
     */
    public String getItemId ()
    {
        return this._itemId;
    }

    /**
     * Get the subscription error or <code>null</code> if there was none
     * @return the subscription error
     */
    public Throwable getSubscriptionError ()
    {
        return this._subscriptionError;
    }
}
