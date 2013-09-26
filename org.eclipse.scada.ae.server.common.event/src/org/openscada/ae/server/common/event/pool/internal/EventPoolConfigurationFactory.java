/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.common.event.pool.internal;

import java.util.Map;

import org.eclipse.scada.ca.ConfigurationDataHelper;
import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPoolConfigurationFactory extends AbstractServiceConfigurationFactory<EventPoolManager>
{

    private final static Logger logger = LoggerFactory.getLogger ( EventPoolConfigurationFactory.class );

    public EventPoolConfigurationFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<EventPoolManager> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.info ( "Creating event pool '{}'", configurationId );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String filter = parameters.get ( "filter" );
        final Integer size = cfg.getIntegerChecked ( "size", "Need 'size' parameter" );

        final EventPoolManager manager = new EventPoolManager ( context, configurationId, filter, size );
        return new Entry<EventPoolManager> ( configurationId, manager );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final EventPoolManager service )
    {
        logger.info ( "Disposing event pool '{}'", id );

        service.dispose ();
    }

    @Override
    protected Entry<EventPoolManager> updateService ( final UserInformation userInformation, final String configurationId, final Entry<EventPoolManager> entry, final Map<String, String> parameters ) throws Exception
    {
        logger.info ( "Updating event pool '{}'", configurationId );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String filter = parameters.get ( "filter" );
        final Integer size = cfg.getIntegerChecked ( "size", "Need 'size' parameter" );

        entry.getService ().update ( filter, size );
        return entry;
    }

}
