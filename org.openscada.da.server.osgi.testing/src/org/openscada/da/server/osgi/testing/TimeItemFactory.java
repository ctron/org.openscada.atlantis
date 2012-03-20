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

package org.openscada.da.server.osgi.testing;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.da.server.common.DataItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class TimeItemFactory extends AbstractServiceConfigurationFactory<TimeDataItem>
{

    private final ScheduledExecutorService executor;

    public TimeItemFactory ( final ScheduledExecutorService executor, final BundleContext context )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<TimeDataItem> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final TimeDataItem dataItem = new TimeDataItem ( configurationId, this.executor );

        dataItem.update ( parameters );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        final ServiceRegistration<DataItem> handle = context.registerService ( DataItem.class, dataItem, properties );

        return new Entry<TimeDataItem> ( configurationId, dataItem, handle );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final TimeDataItem service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<TimeDataItem> updateService ( final UserInformation userInformation, final String configurationId, final Entry<TimeDataItem> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
