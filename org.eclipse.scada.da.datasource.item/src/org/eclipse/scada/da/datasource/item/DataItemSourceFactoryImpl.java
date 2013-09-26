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

package org.eclipse.scada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.da.datasource.DataSource;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolHelper;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class DataItemSourceFactoryImpl extends AbstractServiceConfigurationFactory<DataItemSourceImpl>
{
    public static final String FACTORY_ID = "da.datasource.dataitem";

    private final ObjectPoolImpl<DataSource> objectPool;

    private final Executor executor;

    private final ServiceRegistration<?> objectPoolHandler;

    public DataItemSourceFactoryImpl ( final BundleContext context, final Executor executor )
    {
        super ( context );

        this.executor = executor;

        this.objectPool = new ObjectPoolImpl<DataSource> ();
        this.objectPoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class );
    }

    @Override
    protected Entry<DataItemSourceImpl> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DataItemSourceImpl service = new DataItemSourceImpl ( context, this.executor );

        service.update ( parameters );

        final Dictionary<?, ?> properties = new Hashtable<String, String> ( 1 );
        this.objectPool.addService ( configurationId, service, properties );

        return new Entry<DataItemSourceImpl> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final DataItemSourceImpl service )
    {
        this.objectPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<DataItemSourceImpl> updateService ( final UserInformation userInformation, final String configurationId, final Entry<DataItemSourceImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    public synchronized void dispose ()
    {
        this.objectPoolHandler.unregister ();

        this.objectPool.dispose ();

        super.dispose ();
    }

}
