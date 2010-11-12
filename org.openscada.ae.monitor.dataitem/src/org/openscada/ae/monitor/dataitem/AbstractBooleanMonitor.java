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

package org.openscada.ae.monitor.dataitem;

import java.util.Date;
import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public abstract class AbstractBooleanMonitor extends AbstractDataItemMonitor
{
    protected Boolean value;

    protected Date timestamp;

    public AbstractBooleanMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, poolTracker, eventProcessor, id, prefix, defaultMonitorType );
    }

    protected abstract void update ();

    @Override
    protected void performDataUpdate ( final Builder builder )
    {
        final DataItemValue value = builder.build ();
        this.timestamp = toTimestamp ( value );
        if ( value == null || !value.isConnected () || value.isError () || value.getValue ().isNull () )
        {
            this.value = null;
        }
        else
        {
            this.value = value.getValue ().asBoolean ();
        }

        update ();
    }

}