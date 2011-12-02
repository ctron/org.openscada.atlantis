/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.event;

import java.util.LinkedList;
import java.util.Queue;

import org.openscada.ae.Event;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class EventProcessor
{
    private final SingleServiceTracker<EventService> tracker;

    private final Filter filter;

    private final Queue<Event> eventQueue = new LinkedList<Event> ();

    private final BundleContext context;

    private EventService service;

    public EventProcessor ( final BundleContext context ) throws InvalidSyntaxException
    {
        this ( "(" + Constants.OBJECTCLASS + "=" + EventService.class.getName () + ")", context );
    }

    public EventProcessor ( final Filter filter, final BundleContext context )
    {
        this.filter = filter;
        this.context = context;
        this.tracker = new SingleServiceTracker<EventService> ( this.context, this.filter, new SingleServiceListener<EventService> () {

            @Override
            public void serviceChange ( final ServiceReference<EventService> reference, final EventService service )
            {
                EventProcessor.this.setService ( service );
            }
        } );
    }

    protected synchronized void setService ( final EventService service )
    {
        this.service = service;
        if ( this.service != null )
        {
            publishStoredEvents ( this.service );
        }
    }

    public EventProcessor ( final String filter, final BundleContext context ) throws InvalidSyntaxException
    {
        this ( FrameworkUtil.createFilter ( filter ), context );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    public synchronized void publishEvent ( final Event event )
    {
        final EventService service = this.service;
        if ( service != null )
        {
            service.publishEvent ( event );
        }
        else
        {
            this.eventQueue.add ( event );
        }
    }

    private void publishStoredEvents ( final EventService service )
    {
        Event event = null;
        while ( ( event = this.eventQueue.poll () ) != null )
        {
            service.publishEvent ( event );
        }
    }

}
