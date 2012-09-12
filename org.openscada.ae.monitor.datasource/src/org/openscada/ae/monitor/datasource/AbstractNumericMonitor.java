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

import org.openscada.ae.event.EventProcessor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public abstract class AbstractNumericMonitor extends AbstractVariantMonitor
{

    public AbstractNumericMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String factoryId, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, factoryId, prefix, defaultMonitorType );
    }

    protected abstract void performNumericUpdate ( final Builder builder, final Number value );

    @Override
    protected void performValueUpdate ( final Map<String, Object> context, final Builder builder, final Variant value )
    {
        if ( value.isDouble () )
        {
            performNumericUpdate ( builder, value.asDouble ( null ) );
        }
        else if ( value.isInteger () )
        {
            performNumericUpdate ( builder, value.asInteger ( null ) );
        }
        else if ( value.isLong () )
        {
            performNumericUpdate ( builder, value.asInteger ( null ) );
        }
        else if ( value.isBoolean () )
        {
            performNumericUpdate ( builder, value.asBoolean ( null ) ? 1 : 0 );
        }
        else
        {
            final String str = value.asString ( "" );
            try
            {
                final double d = Double.parseDouble ( str );
                performNumericUpdate ( builder, d );
            }
            catch ( final NumberFormatException e )
            {
                setUnsafe ();
            }
        }
    }

}