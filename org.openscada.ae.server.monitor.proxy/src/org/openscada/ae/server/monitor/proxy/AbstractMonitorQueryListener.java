/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.eclipse.scada.ae.data.MonitorStatusInformation;
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
            performClearAll ();
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    private void performClearAll ()
    {
        final Set<String> removed = new HashSet<String> ( this.dataCache.keySet () );
        this.dataCache.clear ();
        notifyChange ( null, removed );
    }

    protected void handleDataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        logger.debug ( "Data of {} changed - added: @{}, removed: @{}", new Object[] { this.info, addedOrUpdated == null ? -1 : addedOrUpdated.size (), removed == null ? -1 : removed.size () } );

        this.lock.lock ();

        try
        {
            final Set<String> removedIds = new HashSet<String> ();
            if ( full )
            {
                // remember all as removed
                removedIds.addAll ( this.dataCache.keySet () );
                this.dataCache.clear ();
            }

            if ( addedOrUpdated != null )
            {
                for ( final MonitorStatusInformation info : addedOrUpdated )
                {
                    this.dataCache.put ( info.getId (), info );
                    if ( full )
                    {
                        // maybe we need to re-add the item that was removed due to the "full" clear before
                        removedIds.remove ( info.getId () );
                    }
                }
            }
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    if ( this.dataCache.remove ( id ) != null )
                    {
                        removedIds.add ( id );
                    }
                }
            }
            notifyChange ( addedOrUpdated, removed );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    protected void notifyChange ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed )
    {
        if ( this.disposed )
        {
            logger.info ( "We are disposed. Discard event" );
            return;
        }

        // we may only send updates, since we don't know the full state
        this.proxyMonitorQuery.handleDataUpdate ( addedOrUpdated, removed );
    }

    public void dispose ()
    {
        this.lock.lock ();
        if ( this.disposed )
        {
            return;
        }

        // clear all first
        performClearAll ();

        try
        {
            // setting disposed state
            this.disposed = true;
        }
        finally
        {
            this.lock.unlock ();
        }
    }
}
