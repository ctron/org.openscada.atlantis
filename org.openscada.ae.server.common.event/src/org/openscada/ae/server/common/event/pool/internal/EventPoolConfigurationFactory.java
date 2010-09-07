/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class EventPoolConfigurationFactory extends AbstractServiceConfigurationFactory<EventPoolManager>
{

    public EventPoolConfigurationFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<EventPoolManager> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final String filter = parameters.get ( "filter" );
        final Integer size = Integer.parseInt ( parameters.get ( "size" ) );

        final EventPoolManager manager = new EventPoolManager ( context, configurationId, filter, size );
        return new Entry<EventPoolManager> ( configurationId, manager );
    }

    @Override
    protected void disposeService ( final String id, final EventPoolManager service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<EventPoolManager> updateService ( final String configurationId, final Entry<EventPoolManager> entry, final Map<String, String> parameters ) throws Exception
    {
        final String filter = parameters.get ( "filter" );
        final Integer size = Integer.parseInt ( parameters.get ( "size" ) );

        entry.getService ().update ( filter, size );
        return entry;
    }

}
