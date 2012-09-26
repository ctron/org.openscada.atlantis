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

package org.openscada.da.server.osgi.summary;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.datasource.DataSource;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ManageableObjectPool;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class FactoryImpl extends AbstractServiceConfigurationFactory<AttributeDataSourceSummarizer>
{

    public static final String FACTORY_ID = "org.openscada.da.server.osgi.summary.attribute";

    private final Executor executor;

    private final ObjectPoolTracker<DataSource> tracker;

    private final ManageableObjectPool<DataSource> pool;

    public FactoryImpl ( final Executor executor, final BundleContext context, final ObjectPoolTracker<DataSource> tracker, final ManageableObjectPool<DataSource> pool )
    {
        super ( context );
        this.executor = executor;
        this.tracker = tracker;
        this.pool = pool;
    }

    @Override
    protected Entry<AttributeDataSourceSummarizer> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final AttributeDataSourceSummarizer service = new AttributeDataSourceSummarizer ( this.executor, this.tracker );
        service.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );
        this.pool.addService ( configurationId, service, properties );
        return new Entry<AttributeDataSourceSummarizer> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final AttributeDataSourceSummarizer service )
    {
        this.pool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<AttributeDataSourceSummarizer> updateService ( final UserInformation userInformation, final String configurationId, final Entry<AttributeDataSourceSummarizer> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
