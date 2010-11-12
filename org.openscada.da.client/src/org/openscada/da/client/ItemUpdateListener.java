/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
     * attribute change at all. The must be unmodifiable by the receiver
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
