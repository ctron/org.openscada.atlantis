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

package org.openscada.ae.server.monitor.proxy;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.sec.UserInformation;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMonitorQueryFactory extends AbstractServiceConfigurationFactory<ProxyMonitorQuery>
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyMonitorQueryFactory.class );

    public static final String FACTORY_ID = "org.openscada.ae.server.monitor.proxy";

    private final Executor executor;

    public ProxyMonitorQueryFactory ( final BundleContext context, final Executor executor )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<ProxyMonitorQuery> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.info ( "Creating new proxy query: {}", configurationId );

        final ProxyMonitorQuery service = new ProxyMonitorQuery ( context, this.executor );

        final Hashtable<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        final ServiceRegistration<MonitorQuery> handle = context.registerService ( MonitorQuery.class, service, properties );

        service.update ( userInformation, parameters );

        return new Entry<ProxyMonitorQuery> ( configurationId, service, handle );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ProxyMonitorQuery service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ProxyMonitorQuery> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ProxyMonitorQuery> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( userInformation, parameters );
        return null;
    }

}
