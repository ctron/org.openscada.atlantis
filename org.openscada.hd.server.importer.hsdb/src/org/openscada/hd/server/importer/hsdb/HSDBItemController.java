/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.importer.hsdb;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.core.Variant;
import org.openscada.hd.data.HistoricalItemInformation;
import org.openscada.hd.server.common.HistoricalItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class HSDBItemController
{
    private final HSDBValueSource source;

    private final HSDBHistoricalItem item;

    private final ServiceRegistration<HistoricalItem> handle;

    public HSDBItemController ( final String id, final ScheduledExecutorService executor, final BundleContext context, final HSDBValueSource source )
    {
        this.source = source;

        final Map<String, Variant> properties = new HashMap<String, Variant> ();

        final HistoricalItemInformation information = new HistoricalItemInformation ( id, properties );
        this.item = new HSDBHistoricalItem ( executor, source, information );

        final Dictionary<String, Object> serviceProperties = new Hashtable<String, Object> ();
        serviceProperties.put ( Constants.SERVICE_PID, id );
        serviceProperties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        this.handle = context.registerService ( HistoricalItem.class, this.item, serviceProperties );
    }

    public void dispose ()
    {
        this.handle.unregister ();
        this.source.dispose ();
    }
}
