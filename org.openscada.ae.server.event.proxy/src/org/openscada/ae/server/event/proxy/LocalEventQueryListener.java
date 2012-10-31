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

import java.util.concurrent.locks.Lock;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LocalEventQueryListener extends AbstractEventQueryListener
{

    private final static Logger logger = LoggerFactory.getLogger ( LocalEventQueryListener.class );

    private final SingleServiceTracker<EventQuery> tracker;

    private EventQuery service;

    private final SingleServiceListener<EventQuery> queryListener = new SingleServiceListener<EventQuery> () {

        @Override
        public void serviceChange ( final ServiceReference<EventQuery> reference, final EventQuery service )
        {
            setQueryService ( service );
        }
    };

    private final EventListener eventQueryListener = new EventListener () {

        @Override
        public void handleEvent ( final Event[] event )
        {
            LocalEventQueryListener.this.addEvents ( event );
        }
    };

    public LocalEventQueryListener ( final BundleContext context, final String eventQueryId, final ProxyEventQuery proxyEventQuery, final Lock lock ) throws InvalidSyntaxException
    {
        super ( proxyEventQuery, lock, eventQueryId );
        logger.info ( "Creating new listener - query: {}", eventQueryId );

        this.tracker = new SingleServiceTracker<EventQuery> ( context, FilterUtil.createClassAndPidFilter ( EventQuery.class, eventQueryId ), this.queryListener );
        this.tracker.open ();
    }

    @Override
    public void dispose ()
    {
        this.tracker.close ();

        super.dispose ();
    }

    protected void setQueryService ( final EventQuery service )
    {
        this.lock.lock ();
        try
        {
            if ( service == this.service )
            {
                return;
            }

            if ( this.service != null )
            {
                this.service.removeListener ( this.eventQueryListener );
            }

            this.service = service;

            if ( this.service != null )
            {
                service.addListener ( this.eventQueryListener );
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

}