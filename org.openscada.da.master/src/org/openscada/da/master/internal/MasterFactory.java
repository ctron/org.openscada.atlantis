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

package org.openscada.da.master.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPool;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class MasterFactory extends AbstractServiceConfigurationFactory<MasterItemImpl>
{
    public static final String ITEM_ID = "item.id";

    public static final String CONNECTION_ID = "connection.id";

    private final ExecutorService executor;

    private final ObjectPoolImpl dataSourcePool;

    private final ObjectPoolImpl masterItemPool;

    private final ServiceRegistration<ObjectPool> dataSourcePoolHandler;

    private final ServiceRegistration<ObjectPool> masterItemPoolHandler;

    private final ObjectPoolTracker objectPoolTracker;

    public MasterFactory ( final BundleContext context, final ObjectPoolTracker dataSourceTracker )
    {
        super ( context );

        this.objectPoolTracker = dataSourceTracker;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "MasterItemFactory" ) );

        this.dataSourcePool = new ObjectPoolImpl ();
        this.dataSourcePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.dataSourcePool, DataSource.class.getName () );

        this.masterItemPool = new ObjectPoolImpl ();
        this.masterItemPoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.masterItemPool, MasterItem.class.getName () );
    }

    @Override
    protected Entry<MasterItemImpl> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MasterItemImpl service = new MasterItemImpl ( this.executor, context, configurationId, this.objectPoolTracker );

        service.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Master Data Item" );

        this.dataSourcePool.addService ( configurationId, service, properties );
        this.masterItemPool.addService ( configurationId, service, properties );

        return new Entry<MasterItemImpl> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final MasterItemImpl service )
    {
        this.dataSourcePool.removeService ( configurationId, service );
        this.masterItemPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<MasterItemImpl> updateService ( final UserInformation userInformation, final String configurationId, final Entry<MasterItemImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    public synchronized void dispose ()
    {
        super.dispose ();

        this.dataSourcePoolHandler.unregister ();
        this.masterItemPoolHandler.unregister ();

        this.dataSourcePool.dispose ();
        this.masterItemPool.dispose ();

        this.executor.shutdown ();
    }

}
