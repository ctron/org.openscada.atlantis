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

    private DataItemValue _value = new DataItemValue ();

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

    public void register ( final ItemManager itemManager )
    {
        if ( this._itemManager == itemManager )
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

        this._itemManager = itemManager;
        this._itemManager.addItemUpdateListener ( this._itemId, this._listener );
    }

    public void unregister ()
    {
        ItemManager manager;
        ItemUpdateListener listener;

        manager = this._itemManager;
        listener = this._listener;

        this._itemManager = null;
        this._listener = null;

        if ( manager != null )
        {
            manager.removeItemUpdateListener ( this._itemId, listener );
        }
    }

    private void performNotifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {

        final DataItemValue newValue = new DataItemValue ( this._value );

        if ( cache )
        {
            setChanged ();
            newValue.setAttributes ( new HashMap<String, Variant> ( attributes ) );
        }
        else
        {
            AttributesHelper.mergeAttributes ( newValue.getAttributes (), attributes );
        }

        if ( attributes != null )
        {
            setChanged ();
        }

        if ( value != null )
        {
            setChanged ();
            newValue.setValue ( new Variant ( value ) );
        }

        this._value = newValue;
        notifyObservers ( newValue );
    }

    private void performNotifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        final DataItemValue newValue = new DataItemValue ( this._value );
        newValue.setSubscriptionState ( subscriptionState );
        newValue.setSubscriptionError ( subscriptionError );
        this._value = newValue;

        setChanged ();
        notifyObservers ( newValue );
    }

    /**
     * Fetch the current cached value.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current value
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public Variant getValue ()
    {
        return this._value.getValue ();
    }

    /**
     * Get the complete state of the current data item in an atomic operation
     * @return the current state of the data item 
     */
    public DataItemValue getSnapshotValue ()
    {
        return new DataItemValue ( this._value );
    }

    /**
     * Fetch the current cached attributes.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current attributes
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public Map<String, Variant> getAttributes ()
    {
        return this._value.getAttributes ();
    }

    /**
     * Get the subscription state
     * @return the subscription state
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public SubscriptionState getSubscriptionState ()
    {
        return this._value.getSubscriptionState ();
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
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public Throwable getSubscriptionError ()
    {
        return this._value.getSubscriptionError ();
    }
}
