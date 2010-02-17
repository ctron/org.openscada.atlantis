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

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;

/**
 * Notifies a item on the client that data changed.
 * <p>
 * @author jens
 *
 */
public interface ItemUpdateListener
{
    /**
     * A change on the data item occurred.
     * @param value The new value, or <code>null</code> if the value did not change
     * @param attributes The attributes that changed, may be <code>null</code> if no
     * attribute change at all
     * @param cache Indicating that the change came from the cache, this means
     * that the change was not triggered by a device and that <em>all</em>
     * attribute where sent, not only the changed ones
     */
    public void notifyDataChange ( Variant value, Map<String, Variant> attributes, boolean cache );

    /**
     * Notify a change in the subscription change
     * @param subscriptionState the new subscription change
     * @param subscriptionError the new subscription error or <code>null</code> if everything is ok
     */
    public void notifySubscriptionChange ( SubscriptionState subscriptionState, Throwable subscriptionError );
}
