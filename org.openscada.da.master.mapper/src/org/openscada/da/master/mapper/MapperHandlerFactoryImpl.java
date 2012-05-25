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

package org.openscada.da.master.mapper;

import java.util.Map;

import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class MapperHandlerFactoryImpl extends AbstractServiceConfigurationFactory<MapperMasterHandler>
{
    public static final String FACTORY_ID = "org.openscada.da.master.mapper";

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolTracker mapperPoolTracker;

    private final int defaultPriority;

    public MapperHandlerFactoryImpl ( final BundleContext context, final ObjectPoolTracker poolTracker, final ObjectPoolTracker mapperPoolTracker, final int defaultPriority ) throws InvalidSyntaxException
    {
        super ( context );
        this.poolTracker = poolTracker;
        this.mapperPoolTracker = mapperPoolTracker;
        this.defaultPriority = defaultPriority;
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<MapperMasterHandler> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MapperMasterHandler handler = new MapperMasterHandler ( configurationId, this.poolTracker, this.mapperPoolTracker, this.defaultPriority );
        handler.update ( userInformation, parameters );
        return new Entry<MapperMasterHandler> ( configurationId, handler );
    }

    @Override
    protected Entry<MapperMasterHandler> updateService ( final UserInformation userInformation, final String configurationId, final Entry<MapperMasterHandler> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( userInformation, parameters );
        return null;
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final MapperMasterHandler service )
    {
        service.dispose ();
    }

}
