/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItem extends Observable
{
    private static final String PROP_VALUE = "snapshotValue";

    private final static Logger logger = LoggerFactory.getLogger ( DataItem.class );

    private final String itemId;

    /**
     * The item manager
     */
    private ItemManager itemManager;

    /**
     * The stored item value
     */
    private volatile DataItemValue value = new DataItemValue ();

    /**
     * The listener used to register with the item manager
     */
    private final ItemUpdateListener listener;

    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport ( this );

    /**
     * create a new data item structure.
     * <p>
     * Note that the item is initially unconnected
     * @param itemId the id of the item to register later
     */
    public DataItem ( final String itemId )
    {
        this.itemId = itemId;

        // create the item listener
        this.listener = new ItemUpdateListener () {

            @Override
            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
                DataItem.this.performNotifyDataChange ( value, attributes, cache );
            }

            @Override
            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
                DataItem.this.performNotifySubscriptionChange ( subscriptionState, subscriptionError );
            }
        };
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

        if ( connection != null )
        {
            register ( connection );
        }
    }

    public void register ( final ItemManager itemManager )
    {
        if ( this.itemManager == itemManager )
        {
            return;
        }
        unregister ();

        this.itemManager = itemManager;
        this.itemManager.addItemUpdateListener ( this.itemId, this.listener );
    }

    /**
     * Unregister from the currently registered item manager
     */
    public void unregister ()
    {
        final ItemManager manager = this.itemManager;

        this.itemManager = null;

        if ( manager != null )
        {
            manager.removeItemUpdateListener ( this.itemId, this.listener );
        }
    }

    protected void handlePerformNotifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final DataItemValue.Builder newValue = new Builder ( this.value );

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
            newValue.setValue ( value );
        }

        final DataItemValue oldValue = this.value;
        this.value = newValue.build ();

        try
        {
            notifyObservers ( this.value );
            this.propertySupport.firePropertyChange ( PROP_VALUE, oldValue, this.value );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify data change", e );
        }
    }

    protected void performNotifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        handlePerformNotifyDataChange ( value, attributes, cache );
    }

    protected void handlePerformNotifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( this.value );
        newValue.setSubscriptionState ( subscriptionState );
        newValue.setSubscriptionError ( subscriptionError );

        final DataItemValue oldValue = this.value;
        this.value = newValue.build ();

        setChanged ();

        try
        {
            notifyObservers ( this.value );
            this.propertySupport.firePropertyChange ( PROP_VALUE, oldValue, this.value );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify subscription change", e );
        }
    }

    protected void performNotifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        handlePerformNotifySubscriptionChange ( subscriptionState, subscriptionError );
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
        return this.value.getValue ();
    }

    /**
     * Get the complete state of the current data item in an atomic operation
     * @return the current state of the data item 
     */
    public DataItemValue getSnapshotValue ()
    {
        return this.value;
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
        return this.value.getAttributes ();
    }

    /**
     * Get the subscription state
     * @return the subscription state
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public SubscriptionState getSubscriptionState ()
    {
        return this.value.getSubscriptionState ();
    }

    /**
     * Get the item ID
     * @return the item Id
     */
    public String getItemId ()
    {
        return this.itemId;
    }

    /**
     * Get the subscription error or <code>null</code> if there was none
     * @return the subscription error
     * @deprecated You should use {@link #getSnapshotValue()} instead to get a consistent value
     */
    @Deprecated
    public Throwable getSubscriptionError ()
    {
        return this.value.getSubscriptionError ();
    }

    public void addPropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.propertySupport.addPropertyChangeListener ( listener );
    }

    public void addPropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.propertySupport.addPropertyChangeListener ( propertyName, listener );
    }

    public void removePropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.propertySupport.removePropertyChangeListener ( listener );
    }

    public void removePropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.propertySupport.removePropertyChangeListener ( propertyName, listener );
    }

}
