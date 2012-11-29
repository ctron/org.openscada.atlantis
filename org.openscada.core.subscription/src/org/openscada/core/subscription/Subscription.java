/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.subscription;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openscada.core.data.SubscriptionState;

public class Subscription
{
    private final Map<SubscriptionInformation, Object> listeners = new HashMap<SubscriptionInformation, Object> ( 1 );

    private SubscriptionSource source = null;

    private Object topic = null;

    public Subscription ( final Object topic )
    {
        super ();
        this.topic = topic;
    }

    /**
     * Check if the subscription is empty or nor.
     * A subscription is empty if it neither has a subscription source set nor
     * listeners
     * attached to it.
     * 
     * @return <code>true</code> if the subscription is empty,
     *         <code>false</code> otherwise
     */
    public synchronized boolean isEmpty ()
    {
        return this.source == null && this.listeners.isEmpty ();
    }

    /**
     * Check if the subscription is in granted state. This means that no source
     * is connected but there are listeners attached.
     * 
     * @return <code>true</code> if the subscription is in granted state,
     *         <code>false</code> otherwise
     */
    public synchronized boolean isGranted ()
    {
        return this.source == null && !this.listeners.isEmpty ();
    }

    public synchronized void subscribe ( final SubscriptionListener listener, final Object hint )
    {
        final SubscriptionInformation subscriptionInformation = new SubscriptionInformation ( listener, hint );

        if ( this.listeners.containsKey ( subscriptionInformation ) )
        {
            return;
        }
        this.listeners.put ( subscriptionInformation, hint );

        if ( this.source == null )
        {
            listener.updateStatus ( this.topic, SubscriptionState.GRANTED );
        }
        else
        {
            listener.updateStatus ( this.topic, SubscriptionState.CONNECTED );
            this.source.addListener ( Arrays.asList ( subscriptionInformation ) );
        }
    }

    public synchronized void unsubscribe ( final SubscriptionListener listener )
    {
        final SubscriptionInformation subscriptionInformation = new SubscriptionInformation ( listener, null );
        if ( this.listeners.containsKey ( subscriptionInformation ) )
        {
            final Object hint = this.listeners.remove ( subscriptionInformation );
            subscriptionInformation.setHint ( hint );

            if ( this.source != null )
            {
                this.source.removeListener ( Arrays.asList ( subscriptionInformation ) );
            }

            listener.updateStatus ( this.topic, SubscriptionState.DISCONNECTED );
        }
    }

    public synchronized void setSource ( final SubscriptionSource source )
    {
        // We only act on changes
        if ( this.source == source )
        {
            return;
        }

        if ( this.source != null )
        {
            this.source.removeListener ( this.listeners.keySet () );
        }

        final Set<SubscriptionInformation> keys = this.listeners.keySet ();
        if ( source != null )
        {
            for ( final SubscriptionInformation information : keys )
            {
                information.getListener ().updateStatus ( this.topic, SubscriptionState.CONNECTED );
            }
            if ( !keys.isEmpty () )
            {
                source.addListener ( keys );
            }
        }
        else
        {
            for ( final SubscriptionInformation information : keys )
            {
                information.getListener ().updateStatus ( this.topic, SubscriptionState.GRANTED );
            }
        }

        this.source = source;
    }
}
