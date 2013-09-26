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

package org.openscada.da.core.server;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.data.SubscriptionState;

/**
 * Interface for listening to data items
 * 
 * @author Jens Reimann
 */
public interface ItemChangeListener
{
    /**
     * A change on the data item occurred.
     * 
     * @param itemId
     *            The item id that changed
     * @param value
     *            The new value, or <code>null</code> if the value did not
     *            change
     * @param attributes
     *            The attributes that changed, may be <code>null</code> if no
     *            attribute change at all
     * @param cache
     *            Indicating that the change came from the cache, this means
     *            that the change was not triggered by a device and that
     *            <em>all</em> attribute where sent, not only the changed ones
     */
    public void dataChanged ( String itemId, Variant value, Map<String, Variant> attributes, boolean cache );

    /**
     * Indicating a change in the item subscription
     * 
     * @param itemId
     *            The item ID that changed
     * @param subscriptionState
     *            the new subscription state
     */
    public void subscriptionChanged ( String itemId, SubscriptionState subscriptionState );
}
