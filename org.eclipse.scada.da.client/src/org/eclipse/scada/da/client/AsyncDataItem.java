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

package org.eclipse.scada.da.client;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.data.SubscriptionState;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;

/**
 * A data item which performs the notification asynchronously
 * 
 * @author Jens Reimann
 */
public class AsyncDataItem extends DataItem
{

    /**
     * The executor to use
     */
    private final ExecutorService executor;

    public AsyncDataItem ( final String itemId )
    {
        this ( itemId, (ItemManager)null );
    }

    public AsyncDataItem ( final String itemId, final ItemManager connection )
    {
        this ( itemId, connection, Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "AsyncDataItem/" + itemId ) ) );
    }

    public AsyncDataItem ( final String itemId, final ItemManager connection, final ExecutorService executor )
    {
        super ( itemId );

        this.executor = executor;

        if ( connection != null )
        {
            register ( connection );
        }
    }

    @Override
    protected void performNotifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                AsyncDataItem.this.handlePerformNotifyDataChange ( value, attributes, cache );
            }
        } );
    }

    @Override
    protected void performNotifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                AsyncDataItem.this.handlePerformNotifySubscriptionChange ( subscriptionState, subscriptionError );
            }
        } );
    }

    @Override
    protected void finalize () throws Throwable
    {
        this.executor.shutdown ();
        super.finalize ();
    }

}
