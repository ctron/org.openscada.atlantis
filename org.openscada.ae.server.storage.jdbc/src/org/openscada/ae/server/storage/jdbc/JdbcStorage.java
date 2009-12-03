package org.openscada.ae.server.storage.jdbc;

import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.utils.filter.FilterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorage implements Storage
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorage.class );

    private final AtomicReference<JdbcStorageDAO> jdbcStorageDAO = new AtomicReference<JdbcStorageDAO> ();

    private ExecutorService storageQueueProcessor;

    private long shutDownTimeout = 30000;

    public JdbcStorageDAO getJdbcStorageDAO ()
    {
        return jdbcStorageDAO.get ();
    }

    public void setJdbcStorageDAO ( JdbcStorageDAO jdbcStorageDAO )
    {
        this.jdbcStorageDAO.set ( jdbcStorageDAO );
    }

    public long getShutDownTimeout ()
    {
        return shutDownTimeout;
    }

    public void setShutDownTimeout ( long shutDownTimeout )
    {
        this.shutDownTimeout = shutDownTimeout;
    }

    public void start () throws Exception
    {
        logger.info ( "jdbcStorageDAO instanciated" );
        storageQueueProcessor = Executors.newSingleThreadExecutor ( new ThreadFactory () {
            public Thread newThread ( Runnable r )
            {
                return new Thread ( r, "Executor-" + JdbcStorage.class.getCanonicalName () );
            }
        } );
    }

    public void stop () throws Exception
    {
        storageQueueProcessor.shutdown ();
        boolean result = storageQueueProcessor.awaitTermination ( shutDownTimeout, TimeUnit.MILLISECONDS );
        if ( !result )
        {
            logger.error ( "jdbcStorageDAO is shut down, but not all pending operations have been completed!" );
        }
        logger.info ( "jdbcStorageDAO destroyed" );
    }

    public Query query ( String filter ) throws Exception
    {
        logger.debug ( "Query requested {}", filter );
        return new JdbcQuery ( jdbcStorageDAO.get (), new FilterParser ( filter ).getFilter () );
    }

    public Event store ( final Event event )
    {
        final Event result = Event.create ().event ( event ).id ( UUID.randomUUID () ).entryTimestamp ( new GregorianCalendar ().getTime () ).build ();
        logger.debug ( "Save Event to database: " + event );
        storageQueueProcessor.submit ( new Callable<Event> () {
            public Event call ()
            {
                try
                {
                    jdbcStorageDAO.get ().storeEvent ( MutableEvent.fromEvent ( result ) );
                }
                catch ( Exception e )
                {
                    logger.error ( "Exception occured ({}) while saving Event to database: {}", e, event );
                    return result;
                }
                logger.debug ( "Event saved to database: {}", event );
                return result;
            }
        } );
        return result;
    }
}
