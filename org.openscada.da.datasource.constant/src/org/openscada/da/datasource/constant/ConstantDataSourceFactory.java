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

package org.openscada.da.datasource.constant;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.openscada.da.datasource.DataSource;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ConstantDataSourceFactory extends AbstractServiceConfigurationFactory<ConstantDataSource>
{

    private final ExecutorService executor;

    private final ObjectPoolImpl<DataSource> objectPool;

    private final ServiceRegistration<?> poolRegistration;

    public ConstantDataSourceFactory ( final BundleContext context, final ExecutorService executor )
    {
        super ( context );
        this.executor = executor;

        this.objectPool = new ObjectPoolImpl<DataSource> ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class );
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();
        super.dispose ();
    }

    @Override
    protected Entry<ConstantDataSource> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ConstantDataSource service = new ConstantDataSource ( this.executor );
        service.update ( parameters );

        this.objectPool.addService ( configurationId, service, null );

        return new Entry<ConstantDataSource> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ConstantDataSource service )
    {
        this.objectPool.removeService ( configurationId, service );
    }

    @Override
    protected Entry<ConstantDataSource> updateService ( final UserInformation userInformation, final String configurationId, final org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory.Entry<ConstantDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
