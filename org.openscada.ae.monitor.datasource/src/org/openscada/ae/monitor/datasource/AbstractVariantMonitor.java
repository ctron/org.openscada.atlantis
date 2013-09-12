/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.monitor.datasource;

import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.ae.event.EventProcessor;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.MasterItem;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public abstract class AbstractVariantMonitor extends AbstractDemotingMasterItemMonitor
{

    public AbstractVariantMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String factoryId, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, factoryId, prefix, defaultMonitorType );
    }

    @Override
    protected void performDataUpdate ( final Map<String, Object> context, final Builder builder )
    {
        if ( builder.getSubscriptionState () != SubscriptionState.CONNECTED )
        {
            setUnsafe ();
            return;
        }

        final Variant value = builder.getValue ();
        if ( value == null || value.isNull () )
        {
            setUnsafe ();
            return;
        }

        if ( builder.getAttributes () != null )
        {
            final Variant errorAttr = builder.getAttributes ().get ( "error" );
            if ( errorAttr != null && errorAttr.asBoolean () )
            {
                // error attribute set to "true"
                setUnsafe ();
                return;
            }
        }

        performValueUpdate ( context, builder, value );
    }

    protected abstract void performValueUpdate ( Map<String, Object> context, Builder builder, Variant value );

}
