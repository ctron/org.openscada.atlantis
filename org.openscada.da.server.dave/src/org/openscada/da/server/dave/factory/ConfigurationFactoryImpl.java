/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 IBH SYSTEMS GmbH (http://ibh-systems.com)
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

package org.openscada.da.server.dave.factory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.scada.sec.UserInformation;
import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.openscada.da.server.dave.DaveDevice;
import org.osgi.framework.BundleContext;

public class ConfigurationFactoryImpl extends AbstractServiceConfigurationFactory<DaveDevice>
{
    private final BundleContext context;

    public ConfigurationFactoryImpl ( final BundleContext context )
    {
        super ( context, true );
        this.context = context;
    }

    @Override
    protected Entry<DaveDevice> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DaveDevice device = new DaveDevice ( this.context, configurationId, parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( "daveDevice", configurationId );
        return new Entry<DaveDevice> ( configurationId, device, context.registerService ( DaveDevice.class, device, properties ) );
    }

    @Override
    protected Entry<DaveDevice> updateService ( final UserInformation userInformation, final String configurationId, final Entry<DaveDevice> entry, final Map<String, String> parameters ) throws Exception
    {
        // we never get called since are ware createOnly
        return null;
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final DaveDevice service )
    {
        service.dispose ();
    }
}
