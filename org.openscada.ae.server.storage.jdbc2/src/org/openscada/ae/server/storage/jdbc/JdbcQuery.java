package org.openscada.ae.server.storage.jdbc;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.utils.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcQuery implements Query
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcQuery.class );

    private final StorageDao jdbcStorageDao;

    private ResultSet resultSet;

    private Statement statement;

    private boolean hasMore;

    private WeakReference<List<JdbcQuery>> openQueries;

    ScheduledFuture<Boolean> future;

    public JdbcQuery ( StorageDao jdbcStorageDao, Filter filter, ScheduledExecutorService executor, List<JdbcQuery> openQueries ) throws SQLException, NotSupportedException
    {
        openQueries.add ( this );
        this.openQueries = new WeakReference<List<JdbcQuery>> ( openQueries );
        this.jdbcStorageDao = jdbcStorageDao;
        resultSet = jdbcStorageDao.queryEvents ( filter );
        statement = resultSet.getStatement ();
        hasMore = resultSet.next ();
        future = executor.schedule ( new Callable<Boolean> () {
            @Override
            public Boolean call ()
            {
                logger.warn ( "Query '{}' was open for over an hour, or service is being shut down, and will now be closed automatically" );
                dispose ();
                return true;
            }
        }, 1, TimeUnit.HOURS );
    }

    @Override
    public boolean hasMore ()
    {
        return hasMore;
    }

    @Override
    public Collection<Event> getNext ( long count ) throws Exception
    {
        List<Event> result = new ArrayList<Event> ();
        if ( hasMore )
        {
            if ( resultSet.isClosed () )
            {
                throw new RuntimeException ( "ResultSet is closed (probably due to a timeout), please create a new query" );
            }
            hasMore = jdbcStorageDao.toEventList ( resultSet, result, false, count );
        }
        return result;
    }

    @Override
    public void dispose ()
    {
        this.hasMore = false;
        if ( resultSet != null )
        {
            try
            {
                if ( resultSet != null && !resultSet.isClosed () )
                {
                    resultSet.close ();
                }
            }
            catch ( SQLException e )
            {
                logger.warn ( "error on closing database resources", e );
            }
            try
            {
                if ( statement != null && !statement.isClosed () )
                {
                    statement.close ();
                }
            }
            catch ( SQLException e )
            {
                logger.warn ( "error on closing database resources", e );
            }
        }
        List<JdbcQuery> openQueries = this.openQueries.get ();
        if ( openQueries != null )
        {
            openQueries.remove ( this );
        }
        if ( future != null )
        {
            future.cancel ( false );
        }
    }
}
