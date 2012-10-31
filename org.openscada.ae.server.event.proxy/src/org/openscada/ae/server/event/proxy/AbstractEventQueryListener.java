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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractEventQueryListener
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractEventQueryListener.class );

    protected final Lock lock;

    private final ProxyEventQuery proxyEventQuery;

    protected final String info;

    private boolean disposed;

    public AbstractEventQueryListener ( final ProxyEventQuery proxyEventQuery, final Lock lock, final String info )
    {
        this.lock = lock;
        this.proxyEventQuery = proxyEventQuery;
        this.info = info;
    }

    protected void addEvents ( final Event[] events )
    {
        if ( events == null )
        {
            return;
        }

        logger.debug ( "adding events: {}", events.length );

        this.lock.lock ();
        try
        {
            if ( this.disposed )
            {
                logger.info ( "We are disposed. Discard event" );
                return;
            }

            this.proxyEventQuery.addEvents ( events );
        }
        finally
        {
            this.lock.unlock ();
        }
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
