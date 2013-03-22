/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.storage.postgres;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.utils.collection.BoundedPriorityQueueSet;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterParseException;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.osgi.jdbc.CommonConnectionAccessor;
import org.openscada.utils.osgi.jdbc.DataSourceConnectionAccessor;
import org.openscada.utils.osgi.jdbc.pool.PoolConnectionAccessor;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorage extends BaseStorage
{

    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorage.class );

    private final CommonConnectionAccessor accessor;

    private final ScheduledExecutorService scheduler;

    private final List<JdbcQuery> openQueries = new CopyOnWriteArrayList<JdbcQuery> ();

    private final BoundedPriorityQueueSet<Event> errorQueue = new BoundedPriorityQueueSet<Event> ( 1000 );

    private final JdbcDao jdbcDao;

    private ScheduledFuture<?> scheduledProcessErrorQueue;

    private ScheduledFuture<?> scheduledCleanUpJob;

    public JdbcStorage ( final DataSourceFactory dataSourceFactory, final ScheduledExecutorService scheduler, final Properties dbProperties, final boolean usePool, final String schema, final String instance ) throws SQLException
    {
        super ();
        this.accessor = usePool ? new PoolConnectionAccessor ( dataSourceFactory, dbProperties ) : new DataSourceConnectionAccessor ( dataSourceFactory, dbProperties );
        this.jdbcDao = new JdbcDao ( this.accessor, schema, instance, new NodeIdProvider () {
            @Override
            public String getNodeId ()
            {
                return this.getNodeId ();
            }
        } );
        this.scheduler = scheduler;

    }

    public void start ()
    {
        this.scheduledProcessErrorQueue = this.scheduler.scheduleWithFixedDelay ( new Runnable () {
            @Override
            public void run ()
            {
                processErrorQueue ();
            }
        }, 30, 30, TimeUnit.SECONDS );
        this.scheduledCleanUpJob = this.scheduler.scheduleWithFixedDelay ( new CleanUpJob ( this.jdbcDao ), CleanUpJob.getCleanupPeriod (), CleanUpJob.getCleanupPeriod (), TimeUnit.SECONDS );
    }

    public void dispose ()
    {
        if ( this.scheduledProcessErrorQueue != null )
        {
            this.scheduledProcessErrorQueue.cancel ( false );
        }
        if ( this.scheduledCleanUpJob != null )
        {
            this.scheduledCleanUpJob.cancel ( false );
        }
        for ( final JdbcQuery query : this.openQueries )
        {
            query.dispose ();
        }
    }

    @Override
    public Event store ( final Event event, final StoreListener listener )
    {
        // create Event with entry timestamp and new ID
        final Event eventToStore = createEvent ( event );
        doStore ( listener, eventToStore, true );
        return eventToStore;
    }

    private Future<Event> doStore ( final StoreListener listener, final Event eventToStore, final boolean storeInErrorQueue )
    {
        return this.scheduler.submit ( new Callable<Event> () {
            @Override
            public Event call () throws Exception
            {
                try
                {
                    JdbcStorage.this.jdbcDao.store ( eventToStore );
                    if ( isReplication () )
                    {
                        JdbcStorage.this.jdbcDao.storeReplication ( eventToStore );
                    }
                    JdbcStorage.this.accessor.getConnection ().commit ();
                    if ( listener != null )
                    {
                        try
                        {
                            listener.notify ( eventToStore );
                        }
                        catch ( final Exception e )
                        {
                            logger.error ( "call to listener failed", e );
                        }
                    }
                }
                catch ( final Exception e )
                {
                    if ( storeInErrorQueue )
                    {
                        logger.error ( "storing event failed, putting it on error queue", e );
                        JdbcStorage.this.errorQueue.offer ( eventToStore );
                    }
                    else
                    {
                        logger.error ( "storing event failed", e );
                    }
                }
                return eventToStore;
            }
        } );
    }

    @Override
    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception
    {
        final Future<Event> future = this.scheduler.submit ( new Callable<Event> () {
            @Override
            public Event call () throws Exception
            {
                return JdbcStorage.this.jdbcDao.load ( id );
            }
        } );
        final Event event = future.get ( 10, TimeUnit.SECONDS );
        final Event eventToStore = Event.create ().event ( event ).attribute ( Fields.COMMENT, comment ).build ();
        this.scheduler.submit ( new Callable<Event> () {
            @Override
            public Event call () throws Exception
            {
                JdbcStorage.this.jdbcDao.update ( eventToStore );
                if ( listener != null )
                {
                    try
                    {
                        listener.notify ( eventToStore );
                    }
                    catch ( final Exception e )
                    {
                        logger.error ( "call to listener failed", e );
                    }
                }
                return eventToStore;
            }
        } );
        return eventToStore;
    }

    private void processErrorQueue ()
    {
        logger.debug ( "processing error queue, contains approximately {} elements", this.errorQueue.size () );
        final int size = this.errorQueue.size ();
        final Set<Event> eventsNotSaved = new HashSet<Event> ();
        for ( int i = 0; i < size; i++ )
        {
            final Event event = this.errorQueue.poll ();
            if ( event == null )
            {
                break;
            }
            logger.trace ( "try to store event {} again", event );
            try
            {
                // first check if event may exist already in DB
                final Event existingEvent = this.jdbcDao.load ( event.getId () );
                if ( existingEvent != Event.NULL_EVENT )
                {
                    logger.trace ( "event {} was already in database", event.getId () );
                    // ok it was already stored, so we can it ignore
                    continue;
                }
                final Future<Event> future = doStore ( null, event, false );
                future.get ();
            }
            catch ( final Exception e )
            {
                logger.trace ( "storing of event {} failed again", event );
                eventsNotSaved.add ( event );
            }
        }
        // add to queue again
        for ( final Event event : eventsNotSaved )
        {
            this.errorQueue.offer ( event );
        }
    }

    @Override
    public Query query ( final String filter ) throws Exception
    {
        try
        {
            final Filter parsedFilter = new FilterParser ( filter ).getFilter ();
            return new JdbcQuery ( this.jdbcDao, parsedFilter, this.scheduler, this.openQueries );
        }
        catch ( final FilterParseException e )
        {
            logger.error ( "failed to parse filter", e );
            throw e;
        }
    }

    private boolean isReplication ()
    {
        return Boolean.getBoolean ( "org.openscada.ae.server.storage.jdbc.enableReplication" );
    }
}
