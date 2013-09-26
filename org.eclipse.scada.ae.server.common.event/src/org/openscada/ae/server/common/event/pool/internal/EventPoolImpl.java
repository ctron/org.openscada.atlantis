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

package org.openscada.ae.server.common.event.pool.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.ae.filter.internal.EventMatcherImpl;
import org.openscada.ae.server.common.event.AbstractEventQueryImpl;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public class EventPoolImpl extends AbstractEventQueryImpl implements EventListener
{
    private final static Logger logger = LoggerFactory.getLogger ( EventPoolImpl.class );

    private final static int daysToRetrieve = Integer.getInteger ( "org.openscada.ae.common.event.pool.daysToRetrieve", 90 );

    private static final String isoDatePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final DateFormat isoDateFormat = new SimpleDateFormat ( isoDatePattern );

    private final Storage storage;

    private final EventManager eventManager;

    private final String filter;

    private final EventMatcher matcher;

    public EventPoolImpl ( final Executor executor, final Storage storage, final EventManager eventManager, final String filter, final int poolSize )
    {
        super ( executor, poolSize );

        this.storage = storage;
        this.eventManager = eventManager;
        this.filter = filter;
        this.matcher = new EventMatcherImpl ( filter );
    }

    public synchronized void start () throws Exception
    {
        // add to event manager
        this.eventManager.addEventListener ( this );

        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                loadFromStorage ();
            }
        } );
    }

    public synchronized void stop ()
    {
        this.eventManager.removeEventListener ( this );
    }

    private void loadFromStorage ()
    {
        // load initial set from storage, but restrict it to *daysToRetrieve* days
        try
        {
            final long t = System.currentTimeMillis ();
            // retrieve data per day, to restrict database load
            for ( int daysBack = 1; daysBack <= daysToRetrieve; daysBack++ )
            {
                final Calendar calStart = new GregorianCalendar ();
                final Calendar calEnd = new GregorianCalendar ();
                calStart.setTimeInMillis ( t );
                calStart.add ( Calendar.DAY_OF_YEAR, -daysBack );
                calEnd.setTimeInMillis ( t );
                calEnd.add ( Calendar.DAY_OF_YEAR, -daysBack + 1 );
                final StringBuilder filter = new StringBuilder ();
                filter.append ( "(&" );
                filter.append ( this.filter );
                filter.append ( "(sourceTimestamp>=" + isoDateFormat.format ( calStart.getTime () ) + ")" );
                if ( daysBack > 1 )
                {
                    filter.append ( "(sourceTimestamp<" + isoDateFormat.format ( calEnd.getTime () ) + ")" );
                }
                filter.append ( ")" );
                logger.debug ( "load events from filter: " + filter );
                final Query query = this.storage.query ( filter.toString () );
                try
                {
                    int count;
                    synchronized ( this )
                    {
                        count = this.events.getCapacity ();
                    }

                    final Collection<Event> result = query.getNext ( count );

                    logger.debug ( "Loaded {} entries from storage", result.size () );
                    synchronized ( this )
                    {
                        this.events.addAll ( result );

                        final UnmodifiableIterator<List<Event>> it = Iterators.partition ( this.events.iterator (), chunkSize );
                        while ( it.hasNext () )
                        {
                            final List<org.openscada.ae.Event> chunk = it.next ();
                            notifyEvent ( chunk );
                        }
                    }
                }
                finally
                {
                    query.dispose ();
                }
                if ( this.events.size () >= this.events.getCapacity () )
                {
                    return;
                }
            }
            logger.debug ( "load of events complete" );
        }
        catch ( final Exception e )
        {
            logger.error ( "loadFromStorage failed", e );
        }
    }

    @Override
    public synchronized void handleEvent ( final List<Event> events )
    {
        addEvents ( events, this.matcher );
    }

}
