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
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.scada.sec.UserInformation;
import org.openscada.ae.data.MonitorStatusInformation;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.ca.ConfigurationDataHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMonitorQuery extends MonitorQuery
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyMonitorQuery.class );

    private final Map<String, RemoteMonitorQueryListener> remoteListenerMap = new HashMap<String, RemoteMonitorQueryListener> ();

    private final Map<String, LocalMonitorQueryListener> localListenerMap = new HashMap<String, LocalMonitorQueryListener> ();

    private final BundleContext context;

    private final Lock lock = new ReentrantLock ();

    public ProxyMonitorQuery ( final BundleContext context, final Executor executor )
    {
        super ( executor );
        this.context = context;
    }

    @Override
    public synchronized void dispose ()
    {
        this.lock.lock ();
        try
        {
            for ( final RemoteMonitorQueryListener listener : this.remoteListenerMap.values () )
            {
                listener.dispose ();
            }
            this.remoteListenerMap.clear ();
            for ( final LocalMonitorQueryListener listener : this.localListenerMap.values () )
            {
                listener.dispose ();
            }
            this.localListenerMap.clear ();
        }
        finally
        {
            this.lock.unlock ();
        }

        super.dispose ();
    }

    public void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final Set<String> remoteQueryStrings = new HashSet<String> ();
        for ( final Map.Entry<String, String> query : cfg.getPrefixed ( "remote.queries." ).entrySet () )
        {
            remoteQueryStrings.add ( query.getValue () );
        }

        final Set<String> localQueryStrings = new HashSet<String> ();
        for ( final Map.Entry<String, String> query : cfg.getPrefixed ( "local.queries." ).entrySet () )
        {
            localQueryStrings.add ( query.getValue () );
        }

        this.lock.lock ();
        try
        {
            setRemoteQueries ( remoteQueryStrings );
            setLocalQueries ( localQueryStrings );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    private void setLocalQueries ( final Set<String> queryStrings ) throws InvalidSyntaxException
    {
        // remove all which are missing
        final Set<String> current = new HashSet<String> ( this.localListenerMap.keySet () );
        current.removeAll ( queryStrings );
        for ( final String queryString : current )
        {
            final LocalMonitorQueryListener queryListener = this.localListenerMap.remove ( queryString );
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
            final LocalMonitorQueryListener MonitorQueryListener = createLocalQueryListener ( queryString );
            this.localListenerMap.put ( queryString, MonitorQueryListener );
        }
    }

    private void setRemoteQueries ( final Set<String> queryStrings )
    {
        // remove all which are missing
        final Set<String> current = new HashSet<String> ( this.remoteListenerMap.keySet () );
        current.removeAll ( queryStrings );
        for ( final String queryString : current )
        {
            final RemoteMonitorQueryListener queryListener = this.remoteListenerMap.remove ( queryString );
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
            final RemoteMonitorQueryListener MonitorQueryListener = createQueryListener ( tok[0], tok[1] );
            this.remoteListenerMap.put ( queryString, MonitorQueryListener );
        }
    }

    private RemoteMonitorQueryListener createQueryListener ( final String connectionId, final String monitorQueryId )
    {
        return new RemoteMonitorQueryListener ( this.context, connectionId, monitorQueryId, this, this.lock );
    }

    private LocalMonitorQueryListener createLocalQueryListener ( final String monitorQueryId ) throws InvalidSyntaxException
    {
        return new LocalMonitorQueryListener ( this.context, monitorQueryId, this, this.lock );
    }

    void handleDataUpdate ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed )
    {
        logger.debug ( "handleDataUpdate - added: @{}, removed: @{}", new Object[] { addedOrUpdated == null ? -1 : addedOrUpdated.size (), removed == null ? -1 : removed.size () } );
        // we may only send updates
        updateData ( addedOrUpdated, removed, false );
    }
}
