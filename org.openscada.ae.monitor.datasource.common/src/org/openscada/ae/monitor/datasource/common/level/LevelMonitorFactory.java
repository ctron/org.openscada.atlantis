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

package org.openscada.ae.monitor.datasource.common.level;

import java.util.concurrent.Executor;

import org.eclipse.scada.utils.osgi.pool.ManageableObjectPool;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.common.DataItemMonitor;
import org.openscada.ae.monitor.datasource.AbstractMonitorFactory;
import org.openscada.da.master.MasterItem;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public class LevelMonitorFactory extends AbstractMonitorFactory
{

    public static final String FACTORY_ID = "org.openscada.ae.monitor.level";

    private final Executor executor;

    private final Interner<String> stringInterner;

    private final ObjectPoolTracker<MasterItem> poolTracker;

    public LevelMonitorFactory ( final BundleContext context, final ManageableObjectPool<MonitorService> servicePool, final EventProcessor eventProcessor, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker )
    {
        super ( context, servicePool, eventProcessor );
        this.executor = executor;
        this.stringInterner = stringInterner;
        this.poolTracker = poolTracker;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new LevelMonitor ( this.context, this.executor, this.stringInterner, this.poolTracker, eventProcessor, configurationId, FACTORY_ID, "LEVEL" );
    }

}
