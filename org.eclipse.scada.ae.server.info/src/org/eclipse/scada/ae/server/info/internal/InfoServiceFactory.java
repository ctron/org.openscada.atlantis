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

package org.eclipse.scada.ae.server.info.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.ae.monitor.MonitorService;
import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.da.datasource.DataSource;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class InfoServiceFactory extends AbstractServiceConfigurationFactory<InfoService>
{
    public final static String FACTORY_ID = "ae.server.info";

    private final ObjectPoolImpl<DataSource> dataSourcePool;

    private final Executor executor;

    private final ObjectPoolTracker<MonitorService> monitorPoolTracker;

    public InfoServiceFactory ( final BundleContext context, final Executor executor, final ObjectPoolTracker<MonitorService> monitorPoolTracker, final ObjectPoolImpl<DataSource> dataSourcePool )
    {
        super ( context );
        this.executor = executor;
        this.monitorPoolTracker = monitorPoolTracker;
        this.dataSourcePool = dataSourcePool;
    }

    @Override
    protected Entry<InfoService> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final InfoService query = new InfoService ( context, this.executor, this.monitorPoolTracker, this.dataSourcePool );
        query.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );

        return new Entry<InfoService> ( configurationId, query, context.registerService ( InfoService.class.getName (), query, properties ) );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final InfoService service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<InfoService> updateService ( final UserInformation userInformation, final String configurationId, final Entry<InfoService> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return entry;
    }
}
