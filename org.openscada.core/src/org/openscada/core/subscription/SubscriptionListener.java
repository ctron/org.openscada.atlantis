/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.core.subscription;

/**
 * A basic subscription listener that acts as a base interface for all subscriptions.
 * @author Jens Reimann
 *
 */
public interface SubscriptionListener
{
    /**
     * The subscription status update method. It is called by the SubscriptionSource
     * whenever the subscription changed.
     * @param topic The topic that is notified
     * @param subscriptionState The new status of the subscription
     */
    public abstract void updateStatus ( Object topic, SubscriptionState subscriptionState );
}
