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

package org.openscada.da.component.script;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.openscada.da.server.common.DataItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;

public class ScriptComponentFactory extends AbstractServiceConfigurationFactory<ScriptComponent>
{
    private final BundleContext context;

    private final Executor executor;

    private final ObjectPoolImpl<DataItem> objectPool;

    public ScriptComponentFactory ( final Executor executor, final ObjectPoolImpl<DataItem> objectPool, final BundleContext context )
    {
        super ( context );
        this.executor = executor;
        this.objectPool = objectPool;
        this.context = context;
    }

    @Override
    protected Entry<ScriptComponent> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        return new Entry<ScriptComponent> ( configurationId, new ScriptComponent ( this.executor, this.objectPool, configurationId, context, parameters ) );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ScriptComponent service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ScriptComponent> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ScriptComponent> entry, final Map<String, String> parameters ) throws Exception
    {
        return createService ( userInformation, configurationId, this.context, parameters );
    }

}
