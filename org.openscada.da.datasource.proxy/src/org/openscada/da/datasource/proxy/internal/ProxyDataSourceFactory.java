/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.datasource.proxy.internal;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class ProxyDataSourceFactory extends AbstractServiceConfigurationFactory<ProxyDataSource>
{
    private final ExecutorService executor;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl objectPool;

    private final ServiceRegistration poolRegistration;

    public ProxyDataSourceFactory ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = Executors.newSingleThreadExecutor ();

        this.objectPool = new ObjectPoolImpl ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class.getName () );

        this.poolTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();

        this.poolTracker.close ();
        super.dispose ();
        this.executor.shutdown ();

    }

    @Override
    protected Entry<ProxyDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ProxyDataSource dataSource = new ProxyDataSource ( this.poolTracker, this.executor );
        dataSource.update ( parameters );

        this.objectPool.addService ( configurationId, dataSource, null );

        return new Entry<ProxyDataSource> ( configurationId, dataSource );
    }

    @Override
    protected void disposeService ( final String configurationId, final ProxyDataSource service )
    {
        this.objectPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<ProxyDataSource> updateService ( final String configurationId, final Entry<ProxyDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }
}
