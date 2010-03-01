/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ae.event.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.event.EventService;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventServiceImpl implements EventService, EventManager
{

    private final static Logger logger = LoggerFactory.getLogger ( EventServiceImpl.class );

    private final BundleContext context;

    private final SingleServiceTracker storageTracker;

    private final List<Event> writeQueue = new LinkedList<Event> ();

    private final List<Event> eventBuffer = new LinkedList<Event> ();

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    private Storage storage;

    private final StoreListener storeListener;

    public EventServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;

        this.storeListener = new StoreListener () {

            public void notify ( final Event event )
            {
                EventServiceImpl.this.eventStored ( event );
            }
        };

        final Filter filter = FilterUtil.createClassFilter ( Storage.class.getName () );
        this.storageTracker = new SingleServiceTracker ( this.context, filter, new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                setStorage ( (Storage)service );
            }
        } );
        this.storageTracker.open ();
    }

    protected synchronized void eventStored ( final Event event )
    {
        this.writeQueue.remove ( event );
        logger.debug ( "Write queue size - after event: {}", this.writeQueue.size () );
    }

    public void dispose ()
    {
        this.storageTracker.close ();
    }

    protected synchronized void setStorage ( final Storage service )
    {
        this.storage = service;
        this.writeQueue.clear ();

        publishBufferedEvents ();
    }

    private void publishBufferedEvents ()
    {
        logger.info ( "Storing {} recorded events", this.eventBuffer.size () );

        for ( final Event event : this.eventBuffer )
        {
            performStore ( event );
        }
        this.eventBuffer.clear ();
    }

    public synchronized void publishEvent ( final Event event )
    {
        if ( this.storage != null )
        {
            performStore ( event );
        }
        else
        {
            this.eventBuffer.add ( event );
        }
    }

    private void performStore ( final Event event )
    {
        final Event storedEvent = this.storage.store ( event, this.storeListener );
        // add to the write queue
        this.writeQueue.add ( storedEvent );

        logger.debug ( "Write queue size - after store: {}", this.writeQueue.size () );

        // feed the event pools
        for ( final EventListener listener : this.listeners )
        {
            listener.handleEvent ( new Event[] { storedEvent } );
        }
    }

    public synchronized void addEventListener ( final EventListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            listener.handleEvent ( this.writeQueue.toArray ( new Event[this.writeQueue.size ()] ) );
        }
    }

    public synchronized void removeEventListener ( final EventListener listener )
    {
        this.listeners.remove ( listener );
    }

}
