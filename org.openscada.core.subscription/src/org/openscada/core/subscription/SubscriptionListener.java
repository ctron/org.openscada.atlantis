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

import org.openscada.core.data.SubscriptionState;

/**
 * A basic subscription listener that acts as a base interface for all
 * subscriptions.
 * 
 * @author Jens Reimann
 */
public interface SubscriptionListener
{
    /**
     * The subscription status update method. It is called by the
     * SubscriptionSource
     * whenever the subscription changed.
     * 
     * @param topic
     *            The topic that is notified
     * @param subscriptionState
     *            The new status of the subscription
     */
    public abstract void updateStatus ( Object topic, SubscriptionState subscriptionState );
}
