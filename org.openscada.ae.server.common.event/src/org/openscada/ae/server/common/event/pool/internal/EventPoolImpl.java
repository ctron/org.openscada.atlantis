/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.server.common.event.pool.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.ae.filter.internal.EventMatcherImpl;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.collection.BoundedPriorityQueueSet;
import org.openscada.utils.collection.BoundedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public class EventPoolImpl implements EventListener, EventQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( EventPoolImpl.class );

    private final static int daysToRetrieve = 90;

    private final static int chunkSize = 250;

    private static final String isoDatePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final DateFormat isoDateFormat = new SimpleDateFormat ( isoDatePattern );

    private final BoundedQueue<Event> events;

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    private final Storage storage;

    private final EventManager eventManager;

    private final String filter;

    private final EventMatcher matcher;

    private final Executor executor;

    public EventPoolImpl ( final Executor executor, final Storage storage, final EventManager eventManager, final String filter, final int poolSize )
    {
        this.storage = storage;
        this.eventManager = eventManager;
        this.filter = filter;
        this.matcher = new EventMatcherImpl ( filter );
        this.events = new BoundedPriorityQueueSet<Event> ( poolSize, new Comparator<Event> () {
            public int compare ( final Event o1, final Event o2 )
            {
                return Event.comparator.compare ( o2, o1 );
            }
        } );
        this.executor = executor;
    }

    public synchronized void start () throws Exception
    {
        // add to event manager
        this.eventManager.addEventListener ( this );

        // load initial set from storage, but restrict it to *daysToRetrieve* days
        final Calendar cal = new GregorianCalendar ();
        cal.add ( Calendar.DAY_OF_YEAR, -daysToRetrieve );
        final Query query = this.storage.query ( "(&" + this.filter + "(sourceTimestamp>=" + isoDateFormat.format ( cal.getTime () ) + "))" );
        try
        {
            final Collection<Event> result = query.getNext ( this.events.getCapacity () );
            logger.debug ( "Loaded {} entries from storage", result.size () );
            this.events.addAll ( result );

            final UnmodifiableIterator<List<Event>> it = Iterators.partition ( this.events.iterator (), chunkSize );
            while ( it.hasNext () )
            {
                final List<org.openscada.ae.Event> chunk = it.next ();
                notifyEvent ( chunk.toArray ( new Event[chunk.size ()] ) );
            }
        }
        finally
        {
            query.dispose ();
        }
    }

    public synchronized void stop ()
    {
        this.eventManager.removeEventListener ( this );
    }

    public synchronized void handleEvent ( final Event[] events )
    {
        final Set<Event> toNotify = new HashSet<Event> ();
        for ( final Event event : events )
        {
            if ( this.matcher.matches ( event ) )
            {
                if ( this.events.add ( event ) )
                {
                    toNotify.add ( event );
                }
            }
        }
        notifyEvent ( toNotify.toArray ( new Event[toNotify.size ()] ) );
    }

    private void notifyEvent ( final Event[] event )
    {
        final EventListener[] listeners = this.listeners.toArray ( new EventListener[this.listeners.size ()] );
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final EventListener listener : listeners )
                {
                    listener.handleEvent ( event );
                }
            }
        } );
    }

    public synchronized void addListener ( final EventListener eventListener )
    {
        this.listeners.add ( eventListener );

        final UnmodifiableIterator<List<Event>> it = Iterators.partition ( EventPoolImpl.this.events.iterator (), chunkSize );
        while ( it.hasNext () )
        {
            final List<org.openscada.ae.Event> chunk = it.next ();
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    eventListener.handleEvent ( chunk.toArray ( new Event[chunk.size ()] ) );
                }
            } );
        }

    }

    public synchronized void removeListener ( final EventListener eventListener )
    {
        this.listeners.remove ( eventListener );
    }

}
