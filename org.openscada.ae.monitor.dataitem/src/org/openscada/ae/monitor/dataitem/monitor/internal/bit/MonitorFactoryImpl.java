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

package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class MonitorFactoryImpl extends AbstractMonitorFactory
{
    private final ObjectPoolTracker poolTracker;

    private final Executor executor;

    private final int defaultPriority;

    public MonitorFactoryImpl ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final ObjectPoolImpl servicePool, final EventProcessor eventProcessor, final int defaultPriority )
    {
        super ( context, servicePool, eventProcessor );
        this.poolTracker = poolTracker;
        this.executor = executor;
        this.defaultPriority = defaultPriority;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new BooleanAlarmMonitor ( this.context, this.executor, this.poolTracker, eventProcessor, configurationId, this.defaultPriority );
    }

}
