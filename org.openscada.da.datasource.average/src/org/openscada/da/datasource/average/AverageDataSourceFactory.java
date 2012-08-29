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

package org.openscada.da.datasource.average;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.openscada.da.datasource.DataSource;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class AverageDataSourceFactory extends AbstractServiceConfigurationFactory<AverageDataSource>
{
    private final ExecutorService executor;

    private final ObjectPoolTracker<DataSource> poolTracker;

    private final ObjectPoolImpl<AverageDataSource> avgObjectPool;

    private final ObjectPoolImpl<DataSource> dsObjectPool;

    private final ServiceRegistration<?> avgPoolRegistration;

    private final ServiceRegistration<?> dsPoolRegistration;

    public AverageDataSourceFactory ( final BundleContext context, final ExecutorService executor ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = executor;

        this.avgObjectPool = new ObjectPoolImpl<AverageDataSource> ();
        this.avgPoolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.avgObjectPool, AverageDataSource.class );

        this.dsObjectPool = new ObjectPoolImpl<DataSource> ();
        this.dsPoolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.dsObjectPool, DataSource.class );

        this.poolTracker = new ObjectPoolTracker<DataSource> ( context, DataSource.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.dsPoolRegistration.unregister ();
        this.avgPoolRegistration.unregister ();

        this.dsObjectPool.dispose ();
        this.avgObjectPool.dispose ();

        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<AverageDataSource> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final AverageDataSource dataSource = new AverageDataSource ( configurationId, this.poolTracker, this.executor, this.dsObjectPool );
        dataSource.update ( parameters );

        this.avgObjectPool.addService ( configurationId, dataSource, null );

        return new Entry<AverageDataSource> ( configurationId, dataSource );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final AverageDataSource service )
    {
        this.avgObjectPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<AverageDataSource> updateService ( final UserInformation userInformation, final String configurationId, final Entry<AverageDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }
}
