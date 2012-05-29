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

package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.da.datasource.DataSource;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class DataItemTargetFactoryImpl extends AbstractServiceConfigurationFactory<DataItemTargetImpl>
{
    public static final String FACTORY_ID = "da.dataitem.datasource";

    private final BundleContext context;

    private final ObjectPoolTracker<DataSource> poolTracker;

    private final ObjectPoolImpl<DataItem> itemPool;

    private final ServiceRegistration<?> itemPoolHandle;

    public DataItemTargetFactoryImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.itemPool = new ObjectPoolImpl<DataItem> ();

        this.itemPoolHandle = ObjectPoolHelper.registerObjectPool ( context, this.itemPool, DataItem.class );

        this.context = context;
        this.poolTracker = new ObjectPoolTracker<DataSource> ( context, DataSource.class );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.itemPoolHandle.unregister ();
        this.itemPool.dispose ();

        this.poolTracker.close ();

        super.dispose ();
    }

    @Override
    protected Entry<DataItemTargetImpl> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        return createDataItem ( configurationId, context, parameters );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final DataItemTargetImpl service )
    {
        this.itemPool.removeService ( id, service );
        service.dispose ();
    }

    @Override
    protected Entry<DataItemTargetImpl> updateService ( final UserInformation userInformation, final String configurationId, final Entry<DataItemTargetImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        this.itemPool.removeService ( configurationId, entry.getService () );
        entry.getService ().dispose ();

        return createDataItem ( configurationId, this.context, parameters );
    }

    protected Entry<DataItemTargetImpl> createDataItem ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final String itemId = parameters.get ( "item.id" );
        if ( itemId == null )
        {
            throw new IllegalArgumentException ( "'item.id' must be set" );
        }

        final String datasourceId = parameters.get ( "datasource.id" );
        final DataItemTargetImpl item = new DataItemTargetImpl ( this.poolTracker, new DataItemInformationBase ( itemId ), datasourceId );

        final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        final String description = parameters.get ( "description" );
        if ( description != null )
        {
            properties.put ( Constants.SERVICE_DESCRIPTION, description );
        }

        // register
        this.itemPool.addService ( configurationId, item, properties );

        return new Entry<DataItemTargetImpl> ( configurationId, item );
    }
}
