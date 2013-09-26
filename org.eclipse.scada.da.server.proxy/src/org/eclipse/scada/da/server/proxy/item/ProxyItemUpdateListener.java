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

package org.eclipse.scada.da.server.proxy.item;

import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.data.SubscriptionState;
import org.eclipse.scada.da.client.ItemUpdateListener;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.proxy.connection.ProxySubConnection;

public class ProxyItemUpdateListener implements ItemUpdateListener
{
    private final ProxyDataItem item;

    private final ProxySubConnection subConnection;

    private final Executor executor;

    public ProxyItemUpdateListener ( final Executor executor, final ProxyDataItem item, final ProxySubConnection subConnection )
    {
        this.executor = executor;
        this.item = item;
        this.subConnection = subConnection;
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                ProxyItemUpdateListener.this.item.getProxyValueHolder ().updateData ( ProxyItemUpdateListener.this.subConnection.getId (), value, attributes, cache ? AttributeMode.SET : AttributeMode.UPDATE );
            }
        } );

    }

    public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                ProxyItemUpdateListener.this.item.getProxyValueHolder ().updateSubscriptionState ( ProxyItemUpdateListener.this.subConnection.getId (), subscriptionState, subscriptionError );
            }
        } );

    }
}