/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.mapper.osgi.jdbc;

import java.util.Map;

import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ManageableObjectPool;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.openscada.da.mapper.ValueMapper;
import org.openscada.da.server.common.DataItem;
import org.osgi.framework.BundleContext;

public class JdbcValueMapperFactory extends AbstractServiceConfigurationFactory<JdbcValueMapper>
{

    public static final String FACTORY_ID = "org.openscada.da.mapper.osgi.jdbcMapper";

    private final ManageableObjectPool<ValueMapper> pool;

    private final BundleContext context;

    private final ObjectPoolImpl<DataItem> itemPool;

    public JdbcValueMapperFactory ( final BundleContext context, final ManageableObjectPool<ValueMapper> pool, final ObjectPoolImpl<DataItem> itemPool )
    {
        super ( context );
        this.context = context;
        this.pool = pool;
        this.itemPool = itemPool;
    }

    @Override
    protected Entry<JdbcValueMapper> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final JdbcValueMapper service = new JdbcValueMapper ( this.context, configurationId, this.itemPool );
        service.update ( parameters );

        this.pool.addService ( configurationId, service, null );

        return new Entry<JdbcValueMapper> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final JdbcValueMapper service )
    {
        this.pool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<JdbcValueMapper> updateService ( final UserInformation userInformation, final String configurationId, final Entry<JdbcValueMapper> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
