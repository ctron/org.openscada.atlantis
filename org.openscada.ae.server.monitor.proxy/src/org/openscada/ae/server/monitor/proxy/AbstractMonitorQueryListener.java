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
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.openscada.ae.MonitorStatusInformation;
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
            final String[] removed = this.dataCache.keySet ().toArray ( new String[0] );
            this.dataCache.clear ();
            notifyChange ( null, removed );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    public void handleDataChanged ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        logger.debug ( "Data of {} changed - added: @{}, removed: @{}", new Object[] { this.info, addedOrUpdated == null ? -1 : addedOrUpdated.length, removed == null ? -1 : removed.length } );

        this.lock.lock ();

        try
        {
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
            notifyChange ( addedOrUpdated, removed );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    protected void notifyChange ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( this.disposed )
        {
            logger.info ( "We are disposed. Discard event" );
            return;
        }

        this.proxyMonitorQuery.handleDataUpdate ( addedOrUpdated, removed );
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
