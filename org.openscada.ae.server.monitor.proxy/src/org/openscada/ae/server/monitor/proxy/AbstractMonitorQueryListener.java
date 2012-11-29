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

package org.openscada.ae.server.monitor.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.openscada.ae.data.MonitorStatusInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractMonitorQueryListener
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorQueryListener.class );

    private final Map<String, MonitorStatusInformation> dataCache = new HashMap<String, MonitorStatusInformation> ();

    protected final Lock lock;

    private final ProxyMonitorQuery proxyMonitorQuery;

    protected final String info;

    private boolean disposed;

    public AbstractMonitorQueryListener ( final ProxyMonitorQuery proxyMonitorQuery, final Lock lock, final String info )
    {
        this.lock = lock;
        this.proxyMonitorQuery = proxyMonitorQuery;
        this.info = info;
    }

    protected void clearAll ()
    {
        this.lock.lock ();
        try
        {
            this.dataCache.clear ();
            notifyChange ( null, null, true );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    public void handleDataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        logger.debug ( "Data of {} changed - added: @{}, removed: @{}", new Object[] { this.info, addedOrUpdated == null ? -1 : addedOrUpdated.size (), removed == null ? -1 : removed.size () } );

        this.lock.lock ();

        try
        {
            if ( full )
            {
                this.dataCache.clear ();
            }

            if ( addedOrUpdated != null )
            {
                for ( final MonitorStatusInformation info : addedOrUpdated )
                {
                    this.dataCache.put ( info.getId (), info );
                }
            }
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    this.dataCache.remove ( id );
                }
            }
            notifyChange ( addedOrUpdated, removed, full );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    protected void notifyChange ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        if ( this.disposed )
        {
            logger.info ( "We are disposed. Discard event" );
            return;
        }

        this.proxyMonitorQuery.handleDataUpdate ( addedOrUpdated, removed, full );
    }

    public void dispose ()
    {
        this.lock.lock ();
        try
        {
            this.disposed = true;
        }
        finally
        {
            this.lock.unlock ();
        }
    }
}
