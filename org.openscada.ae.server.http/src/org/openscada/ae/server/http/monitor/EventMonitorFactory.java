/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.http.monitor;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Pair;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMonitorFactory extends AbstractServiceConfigurationFactory<EventMonitor> implements AknHandler, EventMonitorEvaluator
{
    public static final Object FACTORY_ID = "ae.monitor.ae.event.external";

    private static final Logger logger = LoggerFactory.getLogger ( EventMonitorFactory.class );

    private final Executor executor;

    private final ObjectPoolImpl servicePool;

    private final EventProcessor eventProcessor;

    private final ConcurrentMap<String, EventMonitor> monitors = new ConcurrentHashMap<String, EventMonitor> ();

    public EventMonitorFactory ( final BundleContext context, final Executor executor, final ObjectPoolImpl servicePool, final EventProcessor eventProcessor )
    {
        super ( context );
        this.executor = executor;
        this.servicePool = servicePool;
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected Entry<EventMonitor> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final EventMonitor instance = new EventMonitorImpl ( context, this.executor, this.eventProcessor, configurationId );

        instance.update ( userInformation, parameters );
        instance.init ();

        this.monitors.put ( configurationId, instance );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        this.servicePool.addService ( configurationId, instance, properties );

        return new Entry<EventMonitor> ( configurationId, instance );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final EventMonitor service )
    {
        this.monitors.remove ( configurationId );
        this.servicePool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<EventMonitor> updateService ( final UserInformation userInformation, final String configurationId, final AbstractServiceConfigurationFactory.Entry<EventMonitor> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( userInformation, parameters );
        return null;
    }

    @Override
    public boolean acknowledge ( final String monitorId, final UserInformation aknUser, final Date aknTimestamp )
    {
        logger.debug ( "Try to process ACK: {}", monitorId );

        final EventMonitor monitor = this.monitors.get ( monitorId );
        if ( monitor != null )
        {
            monitor.akn ( aknUser, aknTimestamp );
        }

        return false;
    }

    @Override
    public Event evaluate ( final Event event )
    {
        for ( final EventMonitor monitor : this.monitors.values () )
        {
            final Pair<Boolean, Event> result = monitor.evaluate ( event );
            if ( result.first )
            {
                return result.second;
            }
        }
        return event;
    }
}