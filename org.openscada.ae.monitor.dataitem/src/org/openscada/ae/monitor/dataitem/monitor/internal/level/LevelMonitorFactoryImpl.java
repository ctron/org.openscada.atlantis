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

package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public class LevelMonitorFactoryImpl extends AbstractMonitorFactory
{

    public static final String FACTORY_PREFIX = "org.openscada.da.level";

    private final String type;

    private final boolean lowerOk;

    private final int priority;

    private final boolean cap;

    private final String defaultMonitorType;

    private final ObjectPoolTracker<MasterItem> poolTracker;

    private final boolean includedOk;

    private final Executor executor;

    private final Interner<String> stringInterner;

    public LevelMonitorFactoryImpl ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final ObjectPoolImpl<MonitorService> servicePool, final EventProcessor eventProcessor, final String type, final String defaultMonitorType, final boolean lowerOk, final boolean includedOk, final int priority, final boolean cap )
    {
        super ( context, servicePool, eventProcessor );
        this.executor = executor;
        this.poolTracker = poolTracker;
        this.type = type;
        this.lowerOk = lowerOk;
        this.includedOk = includedOk;
        this.priority = priority;
        this.cap = cap;
        this.defaultMonitorType = defaultMonitorType;
        this.stringInterner = stringInterner;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new LevelAlarmMonitor ( this.context, this.executor, this.stringInterner, this.poolTracker, eventProcessor, configurationId, FACTORY_PREFIX + "." + this.type, this.defaultMonitorType, this.lowerOk, this.includedOk, this.priority, this.cap );
    }
}
