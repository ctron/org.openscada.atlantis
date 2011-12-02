/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.security.Principal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.da.server.dave.DaveDevice;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class ConfigurationFactoryImpl extends AbstractServiceConfigurationFactory<DaveDevice>
{

    private final Map<String, DaveDevice> devices = new HashMap<String, DaveDevice> ();

    private final BundleContext context;

    public ConfigurationFactoryImpl ( final BundleContext context )
    {
        super ( context );
        this.context = context;
    }

    public synchronized void delete ( final String configurationId ) throws Exception
    {
        final DaveDevice device = this.devices.remove ( configurationId );
        if ( device != null )
        {
            device.dispose ();
        }
    }

    public synchronized void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        DaveDevice device = this.devices.get ( configurationId );
        if ( device == null )
        {
            device = new DaveDevice ( this.context, configurationId, properties );
            this.devices.put ( configurationId, device );
        }
        else
        {
            device.update ( properties );
        }
    }

    @Override
    protected Entry<DaveDevice> createService ( final Principal principal, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DaveDevice device = new DaveDevice ( this.context, configurationId, parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( "daveDevice", configurationId );
        return new Entry<DaveDevice> ( configurationId, device, context.registerService ( DaveDevice.class.getName (), device, properties ) );
    }

    @Override
    protected Entry<DaveDevice> updateService ( final Principal principal, final String configurationId, final Entry<DaveDevice> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    protected void disposeService ( final Principal principal, final String id, final DaveDevice service )
    {
        service.dispose ();
    }
}
