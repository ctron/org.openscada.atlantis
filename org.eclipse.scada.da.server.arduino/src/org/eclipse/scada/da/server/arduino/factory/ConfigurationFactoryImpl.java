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

package org.eclipse.scada.da.server.arduino.factory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.da.server.arduino.ArduinoDevice;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.da.server.common.DataItem;
import org.osgi.framework.BundleContext;

public class ConfigurationFactoryImpl extends AbstractServiceConfigurationFactory<ArduinoDevice>
{
    private final BundleContext context;

    private final ObjectPoolImpl<DataItem> itemPool;

    private final Executor executor;

    public ConfigurationFactoryImpl ( final BundleContext context, final ObjectPoolImpl<DataItem> itemPool, final Executor executor )
    {
        super ( context );
        this.context = context;
        this.itemPool = itemPool;
        this.executor = executor;
    }

    @Override
    protected Entry<ArduinoDevice> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ArduinoDevice device = new ArduinoDevice ( this.context, configurationId, parameters, this.itemPool, this.executor );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( "adrduinoDevice", configurationId );
        return new Entry<ArduinoDevice> ( configurationId, device, context.registerService ( ArduinoDevice.class, device, properties ) );
    }

    @Override
    protected Entry<ArduinoDevice> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ArduinoDevice> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().dispose ();
        entry.getHandle ().unregister ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( "adrduinoDevice", configurationId );
        final ArduinoDevice device = new ArduinoDevice ( this.context, configurationId, parameters, this.itemPool, this.executor );

        return new Entry<ArduinoDevice> ( configurationId, device, this.context.registerService ( ArduinoDevice.class, device, properties ) );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final ArduinoDevice service )
    {
        service.dispose ();
    }
}
