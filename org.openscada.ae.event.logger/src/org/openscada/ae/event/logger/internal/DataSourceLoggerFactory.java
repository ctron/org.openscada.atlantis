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

package org.openscada.ae.event.logger.internal;

import java.util.Map;

import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class DataSourceLoggerFactory extends AbstractServiceConfigurationFactory<MasterItemLogger>
{

    private final ObjectPoolTracker poolTracker;

    public DataSourceLoggerFactory ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.poolTracker = new ObjectPoolTracker ( context, MasterItem.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<MasterItemLogger> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MasterItemLogger logger = new MasterItemLogger ( context, this.poolTracker, 0 );
        logger.update ( parameters );
        return new Entry<MasterItemLogger> ( configurationId, logger );
    }

    @Override
    protected void disposeService ( final String id, final MasterItemLogger service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<MasterItemLogger> updateService ( final String configurationId, final Entry<MasterItemLogger> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
