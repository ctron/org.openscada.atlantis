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

package org.openscada.ae.server.event.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openscada.ae.Event;
import org.openscada.ae.server.common.event.AbstractEventQueryImpl;
import org.openscada.ca.ConfigurationDataHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyEventQuery extends AbstractEventQueryImpl
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyEventQuery.class );

    private final Map<String, RemoteEventQueryListener> remoteListenerMap = new HashMap<String, RemoteEventQueryListener> ();

    private final Map<String, LocalEventQueryListener> localListenerMap = new HashMap<String, LocalEventQueryListener> ();

    private final BundleContext context;

    private final Lock lock = new ReentrantLock ();

    public ProxyEventQuery ( final BundleContext context, final Executor executor, final int poolSize, final Map<String, String> parameters ) throws Exception
    {
        super ( executor, poolSize );
        this.context = context;

        update ( parameters );
    }

    public void dispose ()
    {
        this.lock.lock ();
        logger.info ( "Disposing" );
        try
        {
            for ( final RemoteEventQueryListener listener : this.remoteListenerMap.values () )
            {
                listener.dispose ();
            }
            this.remoteListenerMap.clear ();
            for ( final LocalEventQueryListener listener : this.localListenerMap.values () )
            {
                listener.dispose ();
            }
            this.localListenerMap.clear ();
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    protected void update ( final Map<String, String> parameters ) throws Exception
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
            final LocalEventQueryListener queryListener = this.localListenerMap.remove ( queryString );
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
            final LocalEventQueryListener MonitorQueryListener = createLocalQueryListener ( queryString );
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
            final RemoteEventQueryListener queryListener = this.remoteListenerMap.remove ( queryString );
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
            final RemoteEventQueryListener MonitorQueryListener = createQueryListener ( tok[0], tok[1] );
            this.remoteListenerMap.put ( queryString, MonitorQueryListener );
        }
    }

    private RemoteEventQueryListener createQueryListener ( final String connectionId, final String monitorQueryId )
    {
        return new RemoteEventQueryListener ( this.context, connectionId, monitorQueryId, this, this.lock );
    }

    private LocalEventQueryListener createLocalQueryListener ( final String monitorQueryId ) throws InvalidSyntaxException
    {
        return new LocalEventQueryListener ( this.context, monitorQueryId, this, this.lock );
    }

    public void addEvents ( final Event[] events )
    {
        logger.debug ( "Adding events: {}", events.length );
        addEvents ( events, null );
    }
}
