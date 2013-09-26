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

package org.eclipse.scada.ae.server.common.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.event.EventListener;
import org.eclipse.scada.ae.filter.EventMatcher;
import org.eclipse.scada.utils.collection.BoundedPriorityQueueSet;
import org.eclipse.scada.utils.collection.BoundedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public abstract class AbstractEventQueryImpl implements EventQuery
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractEventQueryImpl.class );

    protected final static int chunkSize = Integer.getInteger ( "org.eclipse.scada.ae.common.event.pool.chunkSize", 100 );

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    protected final Executor executor;

    protected final BoundedQueue<Event> events;

    public AbstractEventQueryImpl ( final Executor executor, final int poolSize )
    {
        this.executor = executor;

        this.events = new BoundedPriorityQueueSet<Event> ( poolSize, new Comparator<Event> () {
            @Override
            public int compare ( final Event o1, final Event o2 )
            {
                return Event.comparator.compare ( o2, o1 );
            }
        } );
    }

    protected void notifyEvent ( final List<Event> event )
    {
        final EventListener[] listeners = this.listeners.toArray ( new EventListener[this.listeners.size ()] );
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                for ( final EventListener listener : listeners )
                {
                    listener.handleEvent ( event );
                }
            }
        } );
    }

    @Override
    public synchronized void addListener ( final EventListener eventListener )
    {
        this.listeners.add ( eventListener );

        final UnmodifiableIterator<List<Event>> it = Iterators.partition ( AbstractEventQueryImpl.this.events.iterator (), chunkSize );
        while ( it.hasNext () )
        {
            final List<org.eclipse.scada.ae.Event> chunk = it.next ();
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    eventListener.handleEvent ( chunk );
                }
            } );
        }

    }

    protected synchronized void addEvents ( final List<Event> events, final EventMatcher matcher )
    {
        final List<Event> toNotify = new ArrayList<Event> ( events.size () );
        for ( final Event event : events )
        {
            if ( matcher == null || matcher.matches ( event ) )
            {
                if ( this.events.add ( event ) )
                {
                    toNotify.add ( event );
                }
            }
        }
        logger.debug ( "new event pool size: {}", this.events.size () );
        notifyEvent ( toNotify );
    }

    @Override
    public synchronized void removeListener ( final EventListener eventListener )
    {
        this.listeners.remove ( eventListener );
    }

    @Override
    public int getCapacity ()
    {
        return this.events.getCapacity ();
    }
}
