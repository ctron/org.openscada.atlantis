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

package org.eclipse.scada.ae.server.event.proxy;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.ae.server.common.event.EventQuery;
import org.eclipse.scada.ca.ConfigurationDataHelper;
import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyEventQueryFactory extends AbstractServiceConfigurationFactory<ProxyEventQuery>
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyEventQueryFactory.class );

    public static final String FACTORY_ID = "org.eclipse.scada.ae.server.event.proxy";

    private final Executor executor;

    public ProxyEventQueryFactory ( final BundleContext context, final Executor executor )
    {
        super ( context, true );
        this.executor = executor;
    }

    @Override
    protected Entry<ProxyEventQuery> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.info ( "Creating new proxy query: {}", configurationId );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final int poolSize = cfg.getIntegerChecked ( "poolSize", "'poolSize' must be set" );
        if ( poolSize <= 0 )
        {
            throw new IllegalArgumentException ( "'poolSize' must be a positive integer greater zero" );
        }

        final ProxyEventQuery service = new ProxyEventQuery ( context, this.executor, poolSize, parameters );

        final Hashtable<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        final ServiceRegistration<EventQuery> handle = context.registerService ( EventQuery.class, service, properties );

        return new Entry<ProxyEventQuery> ( configurationId, service, handle );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ProxyEventQuery service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ProxyEventQuery> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ProxyEventQuery> entry, final Map<String, String> parameters ) throws Exception
    {
        // no-op
        return null;
    }

}
