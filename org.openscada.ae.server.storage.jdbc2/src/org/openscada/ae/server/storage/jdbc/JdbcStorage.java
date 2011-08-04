package org.openscada.ae.server.storage.jdbc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.filter.FilterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorage extends BaseStorage
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorage.class );

    private ScheduledExecutorService executor;

    private final AtomicInteger queueSize = new AtomicInteger ( 0 );

    private StorageDao jdbcStorageDao;

    private List<JdbcQuery> openQueries = new CopyOnWriteArrayList<JdbcQuery> ();

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
                    jdbcStorageDao.storeEvent ( eventToStore );
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    if ( listener != null )
                    {
                        listener.notify ( eventToStore );
                    }
                    logger.debug ( "Event saved to database - remaining in queue: {}, event: {}", JdbcStorage.this.queueSize.get (), event );
                }
                catch ( final Exception e )
                {
                    logger.error ( "Exception occured ({}) while saving Event to database: {}", e, event );
                    logger.info ( "Exception was", e );
                }
            }
        } );
        return eventToStore;
    }

    @Override
    public Query query ( String filter ) throws Exception
    {
        logger.debug ( "Query requested {}", filter );
        return new JdbcQuery ( jdbcStorageDao, new FilterParser ( filter ).getFilter (), executor, openQueries );
    }

    @Override
    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception
    {
        this.queueSize.incrementAndGet ();
        logger.debug ( "Update of comment on event {} with comment '{}'", id, comment );
        final Event event = Event.create ().event ( jdbcStorageDao.loadEvent ( id ) ).attribute ( Fields.COMMENT, comment ).build ();
        this.executor.submit ( new Runnable () {
            @Override
            public void run ()
            {
                try
                {
                    jdbcStorageDao.updateComment ( id, comment );
                    logger.debug ( "Comment saved to database - remaining queue: {}, event: {}", JdbcStorage.this.queueSize.get (), event );
                    JdbcStorage.this.queueSize.decrementAndGet ();
                    if ( listener != null )
                    {
                        listener.notify ( event );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( "Exception occured ({}) while saving Comment to database: {}", e, event );
                    logger.info ( "Exception was", e );
                }
            }
        } );
        return event;
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
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( getClass ().getCanonicalName () ) );
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
        logger.info ( "jdbcStorageDAO destroyed" );
    }

    public void setJdbcStorageDao ( StorageDao jdbcStorageDao )
    {
        this.jdbcStorageDao = jdbcStorageDao;
    }
}
