/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.client.sfp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.core.AttributesHelper;
import org.openscada.core.Variant;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.protocol.sfp.messages.BrowseUpdate;
import org.openscada.protocol.sfp.messages.DataUpdate;
import org.openscada.protocol.sfp.messages.DataUpdate.Entry;
import org.openscada.protocol.sfp.messages.ReadAll;
import org.openscada.protocol.sfp.messages.SubscribeBrowse;

public class ReadAllStrategy
{
    private final ConnectionHandler connectionHandler;

    private final ScheduledFuture<?> pollJob;

    private final Map<String, ItemUpdateListener> itemListeners = new HashMap<String, ItemUpdateListener> ();

    private final Map<String, DataItemValue> cache = new HashMap<> ();

    public ReadAllStrategy ( final ConnectionHandler connectionHandler, final long pollDelay )
    {
        this.connectionHandler = connectionHandler;
        this.connectionHandler.sendMessage ( new SubscribeBrowse () );
        this.pollJob = connectionHandler.getExecutor ().scheduleWithFixedDelay ( new Runnable () {

            @Override
            public void run ()
            {
                triggerReadAll ();
            }
        }, 0, pollDelay, TimeUnit.MILLISECONDS );
    }

    protected void triggerReadAll ()
    {
        this.connectionHandler.sendMessage ( new ReadAll () );
    }

    public void handleMessage ( final Object message )
    {
        if ( message instanceof DataUpdate )
        {
            processDataUpdate ( (DataUpdate)message );
        }
        else if ( message instanceof BrowseUpdate )
        {
            processBrowseUpdate ( (BrowseUpdate)message );
        }
    }

    private void processBrowseUpdate ( final BrowseUpdate message )
    {
        // TODO Auto-generated method stub

    }

    private void processDataUpdate ( final DataUpdate message )
    {
        final Set<String> keys = new HashSet<> ( this.cache.keySet () );

        for ( final DataUpdate.Entry entry : message.getEntries () )
        {
            final DataItemValue value = convert ( entry );
            final String itemId = "" + entry.getRegister ();
            internalUpdate ( itemId, value );
            keys.remove ( itemId );
        }

        // now process the removed keys
        for ( final String removed : keys )
        {
            internalRemove ( removed );
        }
    }

    private DataItemValue convert ( final Entry entry )
    {
        final Map<String, Variant> attributes = new HashMap<> ( 10 );

        attributes.put ( "timestamp", Variant.valueOf ( entry.getTimestamp () ) );

        attributes.put ( "sfp.register", Variant.valueOf ( entry.getRegister () ) );

        attributes.put ( "error", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.ERROR ) ) );
        attributes.put ( "alarm", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.ALARM ) ) );
        attributes.put ( "warning", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.WARNING ) ) );
        attributes.put ( "error.ackRequired", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.ERROR_ACK ) ) );
        attributes.put ( "alarm.ackRequired", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.ALARM_ACK ) ) );
        attributes.put ( "warning.ackRequired", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.WARNING_ACK ) ) );
        attributes.put ( "manual", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.MANUAL_OVERRIDE ) ) );
        attributes.put ( "blocked", Variant.valueOf ( entry.getStates ().contains ( DataUpdate.State.BLOCKED ) ) );

        return new DataItemValue ( entry.getValue (), attributes, SubscriptionState.CONNECTED );
    }

    public void dispose ()
    {
        this.pollJob.cancel ( false );

        for ( final ItemUpdateListener listener : this.itemListeners.values () )
        {
            this.connectionHandler.getExecutor ().execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.notifySubscriptionChange ( SubscriptionState.DISCONNECTED, null );
                }
            } );
        }
        this.itemListeners.clear ();
    }

    public void subscribeItem ( final String itemId )
    {
        // NO-OP
    }

    public void unsubscribeItem ( final String itemId )
    {
        // NO-OP
    }

    public void subscribeAll ( final Set<String> items )
    {
        // NO-OP
    }

    public void setItemUpateListener ( final String itemId, final ItemUpdateListener listener )
    {
        this.itemListeners.put ( itemId, listener );

        final DataItemValue value = this.cache.get ( itemId );

        execute ( new Runnable () {

            @Override
            public void run ()
            {
                if ( value != null )
                {
                    listener.notifySubscriptionChange ( SubscriptionState.CONNECTED, null );
                    listener.notifyDataChange ( value.getValue (), value.getAttributes (), true );
                }
                else
                {
                    listener.notifySubscriptionChange ( SubscriptionState.GRANTED, null );
                }
            }
        } );
    }

    public void setAllItemListeners ( final Map<String, ItemUpdateListener> itemListeners )
    {
        for ( final Map.Entry<String, ItemUpdateListener> entry : itemListeners.entrySet () )
        {
            setItemUpateListener ( entry.getKey (), entry.getValue () );
        }
    }

    protected void execute ( final Runnable command )
    {
        this.connectionHandler.getExecutor ().execute ( command );
    }

    protected void internalRemove ( final String itemId )
    {
        final DataItemValue oldValue = this.cache.remove ( itemId );

        if ( oldValue == null )
        {
            return;
        }

        final ItemUpdateListener listener = this.itemListeners.get ( itemId );
        if ( listener == null )
        {
            // no body is interested in this value
            return;
        }

        execute ( new Runnable () {
            @Override
            public void run ()
            {
                listener.notifySubscriptionChange ( SubscriptionState.GRANTED, null );
            };
        } );
    }

    protected void internalUpdate ( final String itemId, final DataItemValue value )
    {
        final DataItemValue oldValue = this.cache.get ( itemId );

        this.cache.put ( itemId, value );

        final ItemUpdateListener listener = this.itemListeners.get ( itemId );
        if ( listener == null )
        {
            // no body is interested in this value
            return;
        }

        if ( oldValue != null )
        {
            Variant valueChange = null;
            if ( !oldValue.getValue ().equals ( value.getValue () ) )
            {
                valueChange = value.getValue ();
            }

            final Map<String, Variant> attributesChange = AttributesHelper.diff ( oldValue.getAttributes (), value.getAttributes () );

            listener.notifyDataChange ( valueChange, attributesChange.isEmpty () ? null : attributesChange, false );
        }
        else
        {
            execute ( new Runnable () {
                @Override
                public void run ()
                {
                    listener.notifySubscriptionChange ( SubscriptionState.CONNECTED, null );
                    listener.notifyDataChange ( value.getValue (), value.getAttributes (), false );
                }
            } );
        }

    }

}
