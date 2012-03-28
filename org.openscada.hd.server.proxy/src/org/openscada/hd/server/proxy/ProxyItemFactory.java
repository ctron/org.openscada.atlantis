/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.proxy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class ProxyItemFactory extends AbstractServiceConfigurationFactory<ProxyHistoricalItem>
{

    private final Executor executor;

    public ProxyItemFactory ( final BundleContext context, final Executor executor )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<ProxyHistoricalItem> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ProxyHistoricalItem service = new ProxyHistoricalItem ( context, this.executor, configurationId, parameters );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "A proxy to historical items" );
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        final ServiceRegistration<HistoricalItem> handle = context.registerService ( HistoricalItem.class, service, properties );
        return new Entry<ProxyHistoricalItem> ( configurationId, service, handle );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ProxyHistoricalItem service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ProxyHistoricalItem> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ProxyHistoricalItem> entry, final Map<String, String> parameters ) throws Exception
    {
        final BundleContext context = entry.getHandle ().getReference ().getBundle ().getBundleContext ();
        entry.getHandle ().unregister ();
        disposeService ( userInformation, configurationId, entry.getService () );
        return createService ( userInformation, configurationId, context, parameters );
    }
}
