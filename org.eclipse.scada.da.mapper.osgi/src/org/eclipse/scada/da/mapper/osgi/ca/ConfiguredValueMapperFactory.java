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

package org.eclipse.scada.da.mapper.osgi.ca;

import java.util.Map;

import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.da.mapper.ValueMapper;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ManageableObjectPool;
import org.osgi.framework.BundleContext;

public class ConfiguredValueMapperFactory extends AbstractServiceConfigurationFactory<ConfiguredValueMapper>
{

    public static final String FACTORY_ID = "org.eclipse.scada.da.mapper.osgi.configuredMapper";

    private final ManageableObjectPool<ValueMapper> pool;

    public ConfiguredValueMapperFactory ( final BundleContext context, final ManageableObjectPool<ValueMapper> pool )
    {
        super ( context );
        this.pool = pool;
    }

    @Override
    protected Entry<ConfiguredValueMapper> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ConfiguredValueMapper service = new ConfiguredValueMapper ();
        service.update ( parameters );

        this.pool.addService ( configurationId, service, null );

        return new Entry<ConfiguredValueMapper> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ConfiguredValueMapper service )
    {
        this.pool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<ConfiguredValueMapper> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ConfiguredValueMapper> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
