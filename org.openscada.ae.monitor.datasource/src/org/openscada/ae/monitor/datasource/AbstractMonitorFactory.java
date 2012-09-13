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

package org.openscada.ae.monitor.datasource;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.common.DataItemMonitor;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ManageableObjectPool;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMonitorFactory extends AbstractServiceConfigurationFactory<DataItemMonitor> implements AknHandler
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorFactory.class );

    protected final BundleContext context;

    protected abstract DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor );

    protected final EventProcessor eventProcessor;

    private final ManageableObjectPool<MonitorService> servicePool;

    public AbstractMonitorFactory ( final BundleContext context, final ManageableObjectPool<MonitorService> servicePool, final EventProcessor eventProcessor )
    {
        super ( context );
        this.context = context;
        this.servicePool = servicePool;
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected Entry<DataItemMonitor> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DataItemMonitor instance = createInstance ( configurationId, this.eventProcessor );

        instance.update ( userInformation, parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        this.servicePool.addService ( configurationId, instance, properties );

        return new Entry<DataItemMonitor> ( configurationId, instance );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final DataItemMonitor service )
    {
        this.servicePool.removeService ( id, service );
        service.dispose ();
    }

    @Override
    protected Entry<DataItemMonitor> updateService ( final UserInformation userInformation, final String configurationId, final Entry<DataItemMonitor> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( userInformation, parameters );
        return null;
    }

    @Override
    public synchronized boolean acknowledge ( final String monitorId, final UserInformation aknUser, final Date aknTimestamp )
    {
        logger.debug ( "Try to process ACK: {}", monitorId );

        final Entry<DataItemMonitor> entry = getService ( monitorId );
        if ( entry != null )
        {
            entry.getService ().akn ( aknUser, aknTimestamp );
            return true;
        }
        else
        {
            logger.info ( "Monitor '{}' could not be found in this factory ({})", monitorId, getClass () );
        }

        return false;
    }

}