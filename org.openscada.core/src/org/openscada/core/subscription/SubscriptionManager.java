/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manage subscriptions.
 * 
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class SubscriptionManager
{
    private final Map<Object, Subscription> subscriptions = new HashMap<Object, Subscription> ();

    private SubscriptionValidator validator = null;

    /**
     * Unsibscribe from all subscriptions that the listener has subscribed to
     * @param listener the listener to unsubscribe
     */
    public synchronized void unsubscribeAll ( final SubscriptionListener listener )
    {
        for ( final Iterator<Map.Entry<Object, Subscription>> i = this.subscriptions.entrySet ().iterator (); i.hasNext (); )
        {
            final Map.Entry<Object, Subscription> entry = i.next ();
            entry.getValue ().unsubscribe ( listener );

            if ( entry.getValue ().isEmpty () )
            {
                i.remove ();
            }
        }
    }

    /**
     * Subscribe to a topic.
     * @param topic The topic to which the subscription should be made
     * @param listener The listener which will receive the events
     * @throws ValidationException thrown if the subscription cannot be establed (e.g. the topic is invalid)
     */
    public synchronized void subscribe ( final Object topic, final SubscriptionListener listener ) throws ValidationException
    {
        subscribe ( topic, listener, null );
    }

    /**
     * Subscribe to a topic with a hint
     * @param topic The topic to which the subscription should be made
     * @param listener The listener which will receive the events
     * @param hint The hint is specific to the topic
     * @throws ValidationException thrown if the subscription cannot be established (e.g. the topic is invalid)
     */
    public synchronized void subscribe ( final Object topic, final SubscriptionListener listener, final Object hint ) throws ValidationException
    {
        // If we have a validator then do validate
        SubscriptionValidator v;
        if ( ( v = this.validator ) != null )
        {
            if ( !v.validate ( listener, topic ) )
            {
                throw new ValidationException ();
            }
        }

        // Get subscription or create one if there is none
        Subscription s = this.subscriptions.get ( topic );
        if ( s == null )
        {
            s = new Subscription ( topic );
            this.subscriptions.put ( topic, s );
        }

        s.subscribe ( listener, hint );
    }

    public synchronized void unsubscribe ( final Object topic, final SubscriptionListener listener )
    {
        final Subscription s = this.subscriptions.get ( topic );
        if ( s == null )
        {
            return;
        }

        s.unsubscribe ( listener );

        // if the subscription is empty we can erase it
        if ( s.isEmpty () )
        {
            this.subscriptions.remove ( topic );
        }
    }

    public void setValidator ( final SubscriptionValidator validator )
    {
        this.validator = validator;
    }

    /**
     * Set a source for a topic.
     * 
     * This will cause all granted subscriptions to switch to connected for this source
     * @param topic the topic
     * @param source the source to set
     */
    public synchronized void setSource ( final Object topic, final SubscriptionSource source )
    {
        Subscription s = this.subscriptions.get ( topic );
        if ( s == null && source == null )
        {
            return;
        }

        if ( s == null )
        {
            s = new Subscription ( topic );
            this.subscriptions.put ( topic, s );
        }

        s.setSource ( source );

        if ( s.isEmpty () )
        {
            this.subscriptions.remove ( topic );
        }
    }

    /**
     * Get the number of subscriptions currently registered
     * @return the number of subscriptions
     */
    public int getSubscriptionCount ()
    {
        return this.subscriptions.size ();
    }

    /**
     * Get all topic whose subscription is in granted state.
     * @return The list of topics whose subscription is in granted state.
     */
    public synchronized List<Object> getAllGrantedTopics ()
    {
        final List<Object> topicList = new LinkedList<Object> ();

        for ( final Map.Entry<Object, Subscription> entry : this.subscriptions.entrySet () )
        {
            if ( entry.getValue ().isGranted () )
            {
                topicList.add ( entry.getKey () );
            }
        }

        return topicList;
    }
}
