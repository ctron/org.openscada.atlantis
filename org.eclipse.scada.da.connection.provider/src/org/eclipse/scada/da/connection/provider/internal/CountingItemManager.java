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

package org.eclipse.scada.da.connection.provider.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.scada.core.info.StatisticsImpl;
import org.eclipse.scada.da.client.ItemManager;
import org.eclipse.scada.da.client.ItemUpdateListener;

public class CountingItemManager implements ItemManager
{

    public final static Object REGISTERED_ITEMS = new Object ();

    private final ItemManager itemManager;

    private final StatisticsImpl statistics;

    private final Map<SubscriptionItemEntry, Object> itemRegistrationSet = new ConcurrentHashMap<SubscriptionItemEntry, Object> ();

    public CountingItemManager ( final ItemManager itemManager, final StatisticsImpl statistics )
    {
        this.itemManager = itemManager;
        this.statistics = statistics;
    }

    @Override
    public void addItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
    {
        addItemRegistration ( itemName, listener );
        this.itemManager.addItemUpdateListener ( itemName, listener );
    }

    @Override
    public void removeItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
    {
        removeItemRegistration ( itemName, listener );
        this.itemManager.removeItemUpdateListener ( itemName, listener );
    }

    protected void addItemRegistration ( final String itemId, final ItemUpdateListener listener )
    {
        this.itemRegistrationSet.put ( new SubscriptionItemEntry ( itemId, listener ), Boolean.TRUE );
        this.statistics.setCurrentValue ( REGISTERED_ITEMS, this.itemRegistrationSet.size () );
    }

    protected void removeItemRegistration ( final String itemId, final ItemUpdateListener listener )
    {
        this.itemRegistrationSet.remove ( new SubscriptionItemEntry ( itemId, listener ) );
        this.statistics.setCurrentValue ( REGISTERED_ITEMS, this.itemRegistrationSet.size () );
    }

}