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

package org.eclipse.scada.ae.server.event.proxy;

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.client.EventListener;
import org.eclipse.scada.ae.connection.provider.ConnectionService;
import org.eclipse.scada.core.connection.provider.ConnectionIdTracker;
import org.eclipse.scada.core.connection.provider.ConnectionTracker.Listener;
import org.eclipse.scada.core.data.SubscriptionState;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteEventQueryListener extends AbstractEventQueryListener implements Listener, EventListener
{
    private final static Logger logger = LoggerFactory.getLogger ( RemoteEventQueryListener.class );

    private final ConnectionIdTracker tracker;

    private ConnectionService connection;

    private final String eventQueryId;

    public RemoteEventQueryListener ( final BundleContext context, final String connectionId, final String eventQueryId, final ProxyEventQuery proxyEventQuery, final Lock lock )
    {
        super ( proxyEventQuery, lock, connectionId + "#" + eventQueryId );
        logger.info ( "Creating new listener - connection: {}, query: {}", connectionId, eventQueryId );

        this.eventQueryId = eventQueryId;

        this.tracker = new ConnectionIdTracker ( context, connectionId, this, ConnectionService.class );
        this.tracker.open ();
    }

    @Override
    public void dispose ()
    {
        this.tracker.close ();

        super.dispose ();
    }

    @Override
    public void setConnection ( final org.eclipse.scada.core.connection.provider.ConnectionService connectionService )
    {
        logger.debug ( "Setting connection: {}", connectionService );

        this.lock.lock ();
        try
        {
            if ( this.connection != null )
            {
                this.connection.getConnection ().setEventListener ( this.eventQueryId, null );
            }

            this.connection = (ConnectionService)connectionService;

            if ( this.connection != null )
            {
                this.connection.getConnection ().setEventListener ( this.eventQueryId, this );
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    @Override
    public void statusChanged ( final SubscriptionState state )
    {
        logger.info ( "State of {} changed: {}", this.info, state );
    }

    @Override
    public void dataChanged ( final List<Event> addedEvents )
    {
        addEvents ( addedEvents );
    }
}