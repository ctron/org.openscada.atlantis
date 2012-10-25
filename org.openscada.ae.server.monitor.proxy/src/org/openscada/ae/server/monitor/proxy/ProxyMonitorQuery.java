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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMonitorQuery extends MonitorQuery
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyMonitorQuery.class );

    private final Map<String, MonitorQueryListener> listenerMap = new HashMap<String, MonitorQueryListener> ();

    private final BundleContext context;

    private final Lock lock = new ReentrantLock ();

    public ProxyMonitorQuery ( final BundleContext context, final Executor executor )
    {
        super ( executor );
        this.context = context;
    }

    public void update ( final UserInformation userInformation, final Map<String, String> parameters )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final Set<String> queryStrings = new HashSet<String> ();
        for ( final Map.Entry<String, String> query : cfg.getPrefixed ( "queries." ).entrySet () )
        {
            queryStrings.add ( query.getValue () );
        }

        this.lock.lock ();
        try
        {
            setSourceQueries ( queryStrings );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    private void setSourceQueries ( final Set<String> queryStrings )
    {
        // remove all which are missing
        final Set<String> current = new HashSet<String> ( this.listenerMap.keySet () );
        current.removeAll ( queryStrings );
        for ( final String queryString : current )
        {
            final MonitorQueryListener queryListener = this.listenerMap.remove ( queryString );
            if ( queryListener != null )
            {
                logger.info ( "Disposing query: {}", queryString );
                queryListener.dispose ();
            }
        }

        // now add the new ones
        for ( final String queryString : queryStrings )
        {
            logger.info ( "Adding query: {}", queryString );
            final String[] tok = queryString.split ( "#", 2 );
            final MonitorQueryListener MonitorQueryListener = createQueryListener ( tok[0], tok[1] );
            this.listenerMap.put ( queryString, MonitorQueryListener );
        }
    }

    private MonitorQueryListener createQueryListener ( final String connectionId, final String monitorQueryId )
    {
        return new MonitorQueryListener ( this.context, connectionId, monitorQueryId, this, this.lock );
    }

    public void handleDataUpdate ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        logger.debug ( "handleDataUpdate - added: @{}, removed: @{}", new Object[] { addedOrUpdated == null ? -1 : addedOrUpdated.length, removed == null ? -1 : removed.length } );
        updateData ( addedOrUpdated, removed );
    }
}
