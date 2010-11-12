/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.core.Variant;
import org.openscada.utils.filter.FilterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JdbcStorage} is a thin wrapper around the {@link JdbcStorageDAO} which provides just 
 * the basic methods to store Events. An event is converted to a {@link MutableEvent}
 * and then placed on a queue to store.
 * 
 * @author jrose
 */
public class JdbcStorage extends BaseStorage
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorage.class );

    private final AtomicReference<JdbcStorageDAO> jdbcStorageDAO = new AtomicReference<JdbcStorageDAO> ();

    private ExecutorService storageQueueProcessor;

    private long shutDownTimeout = 30000;

    private final AtomicInteger queueSize = new AtomicInteger ( 0 );

    public JdbcStorageDAO getJdbcStorageDAO ()
    {
        return this.jdbcStorageDAO.get ();
    }

    public void setJdbcStorageDAO ( final JdbcStorageDAO jdbcStorageDAO )
    {
        this.jdbcStorageDAO.set ( jdbcStorageDAO );
    }

    public long getShutDownTimeout ()
    {
        return this.shutDownTimeout;
    }

    public void setShutDownTimeout ( final long shutDownTimeout )
    {
        this.shutDownTimeout = shutDownTimeout;
    }

    /**
     * is called by Spring when {@link JdbcStorage} is initialized. It creates a
     * new {@link ExecutorService} which is used to schedule the events for storage.
     *  
     * @throws Exception
     */
    public void start () throws Exception
    {
        logger.info ( "jdbcStorageDAO instanciated" );
        this.storageQueueProcessor = Executors.newSingleThreadExecutor ( new ThreadFactory () {
            public Thread newThread ( final Runnable r )
            {
                return new Thread ( r, "Executor-" + JdbcStorage.class.getCanonicalName () );
            }
        } );
    }

    /**
     * is called by Spring when {@link JdbcStorage} is destroyed. It halts the
     * {@link ExecutorService} and tries to process the remaining events (say, store them
     * to the database).
     * 
     * @throws Exception
     */
    public void stop () throws Exception
    {
        final List<Runnable> openTasks = this.storageQueueProcessor.shutdownNow ();
        final int numOfOpenTasks = openTasks.size ();
        if ( numOfOpenTasks > 0 )
        {
            int numOfOpenTasksRemaining = numOfOpenTasks;
            logger.info ( "jdbcStorageDAO is beeing shut down, but there are still {} events to store", numOfOpenTasks );
            for ( final Runnable runnable : openTasks )
            {
                runnable.run ();
                numOfOpenTasksRemaining -= 1;
                logger.debug ( "jdbcStorageDAO is beeing shut down, but there are still {} events to store", numOfOpenTasksRemaining );
            }
        }
        logger.info ( "jdbcStorageDAO destroyed" );
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Storage#query(java.lang.String)
     */
    public Query query ( final String filter ) throws Exception
    {
        logger.debug ( "Query requested {}", filter );
        return new JdbcQuery ( this.jdbcStorageDAO.get (), new FilterParser ( filter ).getFilter () );
    }

    /**
     * the events are not actually stored within this method, rather given an 
     * {@link ExecutorService} and stored later. This guarantees a immediate return
     * of this method.
     * 
     * @see org.openscada.ae.server.storage.Storage#store(org.openscada.ae.Event)
     */
    public Event store ( final Event event, final StoreListener listener )
    {
        this.queueSize.incrementAndGet ();
        final Event eventToStore = createEvent ( event );
        logger.debug ( "Save Event to database: " + event );
        this.storageQueueProcessor.submit ( new Callable<Boolean> () {
            public Boolean call ()
            {
                try
                {
                    JdbcStorage.this.jdbcStorageDAO.get ().storeEvent ( MutableEvent.fromEvent ( eventToStore ) );
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    if ( listener != null )
                    {
                        listener.notify ( eventToStore );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( "Exception occured ({}) while saving Event to database: {}", e, event );
                    logger.info ( "Exception was", e );
                    return false;
                }
                logger.debug ( "Event saved to database - remaining queue: {}, event: {}", JdbcStorage.this.queueSize.get (), event );
                return true;
            }
        } );
        return eventToStore;
    }

    private Event updateInternal ( final UUID id, final Variant comment, final StoreListener listener ) throws Exception
    {
        final MutableEvent eventToUpdate = getJdbcStorageDAO ().loadEvent ( id );
        eventToUpdate.getAttributes ().put ( Fields.COMMENT.getName (), comment );
        final Event event = MutableEvent.toEvent ( eventToUpdate );
        logger.debug ( "Update Event comment in database: " + event );
        this.storageQueueProcessor.submit ( new Callable<Boolean> () {
            public Boolean call ()
            {
                try
                {
                    JdbcStorage.this.jdbcStorageDAO.get ().storeEvent ( eventToUpdate );
                    if ( listener != null )
                    {
                        listener.notify ( event );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( "Exception occured ({}) while updating comment of Event in database: {}", e, event );
                    logger.info ( "Exception was", e );
                    return false;
                }
                logger.debug ( "Event updated in database: {}", event );
                return true;
            }
        } );
        return event;
    }

    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception
    {
        return updateInternal ( id, new Variant ( comment ), listener );
    }
}
