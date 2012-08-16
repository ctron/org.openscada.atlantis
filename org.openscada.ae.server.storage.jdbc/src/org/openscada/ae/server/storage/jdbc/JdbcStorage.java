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

package org.openscada.ae.server.storage.jdbc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.utils.collection.BoundedPriorityQueueSet;
import org.openscada.utils.concurrent.ScheduledExportedExecutorService;
import org.openscada.utils.filter.FilterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorage extends BaseStorage
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorage.class );

    private ScheduledExecutorService executor;

    private final AtomicInteger queueSize = new AtomicInteger ( 0 );

    private final StorageDao jdbcStorageDao;

    private final List<JdbcQuery> openQueries = new CopyOnWriteArrayList<JdbcQuery> ();

    private final BoundedPriorityQueueSet<Event> errorQueue = new BoundedPriorityQueueSet<Event> ( 1000 );

    public JdbcStorage ( final StorageDao jdbcStorageDao )
    {
        this.jdbcStorageDao = jdbcStorageDao;
    }

    @Override
    public Event store ( final Event event, final StoreListener listener )
    {
        this.queueSize.incrementAndGet ();
        final Event eventToStore = createEvent ( event );
        logger.debug ( "Save Event to database: " + event );
        this.executor.submit ( new Runnable () {
            @Override
            public void run ()
            {
                try
                {
                    JdbcStorage.this.jdbcStorageDao.storeEvent ( eventToStore );
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    if ( listener != null )
                    {
                        listener.notify ( eventToStore );
                    }
                    logger.debug ( "Event saved to database - remaining in queue: {}, event: {}", JdbcStorage.this.queueSize.get (), event );
                }
                catch ( final Exception e )
                {
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    JdbcStorage.this.errorQueue.offer ( eventToStore );
                    logger.error ( "Exception occured ({}) while saving Event to database: {}", e, event );
                    logger.info ( "Exception was", e );
                }
            }
        } );
        return eventToStore;
    }

    private void drainErrorQueue ()
    {
        final int size = this.errorQueue.size ();
        final Set<Event> eventsNotSaved = new HashSet<Event> ();
        for ( int i = 0; i < size; i++ )
        {
            final int ii = i;
            final Event event = this.errorQueue.poll ();
            if ( event != null )
            {
                this.executor.submit ( new Runnable () {
                    @Override
                    public void run ()
                    {
                        try
                        {
                            final Event existingEvent = JdbcStorage.this.jdbcStorageDao.loadEvent ( event.getId () );
                            if ( existingEvent == null )
                            {
                                JdbcStorage.this.jdbcStorageDao.storeEvent ( event );
                                logger.debug ( "Event saved to database which could not be saved before - remaining in queue: {}, event: {}", size - ii, event );
                            }
                        }
                        catch ( final Exception e )
                        {
                            eventsNotSaved.add ( event );
                            logger.error ( "Exception occured ({}) while saving Event to database: {}", e, event );
                            logger.info ( "Exception was", e );
                        }
                    }
                } );
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
        logger.debug ( "Query requested {}", filter );
        return new JdbcQuery ( this.jdbcStorageDao, new FilterParser ( filter ).getFilter (), this.executor, this.openQueries );
    }

    @Override
    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception
    {
        this.queueSize.incrementAndGet ();
        logger.debug ( "Update of comment on event {} with comment '{}'", id, comment );
        final Event event = Event.create ().event ( this.jdbcStorageDao.loadEvent ( id ) ).attribute ( Fields.COMMENT, comment ).build ();
        this.executor.submit ( new Runnable () {
            @Override
            public void run ()
            {
                try
                {
                    JdbcStorage.this.jdbcStorageDao.updateComment ( id, comment );
                    logger.debug ( "Comment saved to database - remaining queue: {}, event: {}", JdbcStorage.this.queueSize.get (), event );
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    if ( listener != null )
                    {
                        listener.notify ( event );
                    }
                }
                catch ( final Exception e )
                {
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    logger.error ( "Exception occured ({}) while saving Comment to database: {}", e, event );
                    logger.info ( "Exception was", e );
                }
            }
        } );
        return event;
    }

    /**
     * Initialize the instance
     * 
     * @throws Exception
     */
    public void start () throws Exception
    {
        logger.info ( "jdbcStorageDAO instanciated" );
        this.executor = new ScheduledExportedExecutorService ( getClass ().getCanonicalName (), 1 );
        // try to store events which could not be stored before
        this.executor.scheduleAtFixedRate ( new Runnable () {
            @Override
            public void run ()
            {
                drainErrorQueue ();
            }
        }, 10000, 10000, TimeUnit.SECONDS );
    }

    /**
     * Dispose the object and free resources
     */
    public void dispose ()
    {
        final List<Runnable> openTasks = this.executor.shutdownNow ();
        final int numOfOpenTasks = openTasks.size ();
        if ( numOfOpenTasks > 0 )
        {
            int numOfOpenTasksRemaining = numOfOpenTasks;
            logger.info ( "jdbcStorageDAO is beeing shut down, but there are still {} open tasks", numOfOpenTasks );
            for ( final Runnable runnable : openTasks )
            {
                runnable.run ();
                numOfOpenTasksRemaining -= 1;
                logger.debug ( "jdbcStorageDAO is beeing shut down, but there are still {} open tasks", numOfOpenTasksRemaining );
            }
        }

        this.jdbcStorageDao.dispose ();

        logger.info ( "jdbcStorageDAO destroyed" );
    }

}
