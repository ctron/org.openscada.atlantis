package org.openscada.hd.server.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.HistoricalItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test2ItemImpl implements HistoricalItem
{
    private final static Logger logger = LoggerFactory.getLogger ( Test2ItemImpl.class );

    private final Set<Test2QueryImpl> queries = new HashSet<Test2QueryImpl> ();

    private final ScheduledExecutorService executor;

    public Test2ItemImpl ()
    {
        this.executor = Executors.newScheduledThreadPool ( 1 );
        this.executor.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                Test2ItemImpl.this.tick ();
            }
        }, 100, 100, TimeUnit.MILLISECONDS );
    }

    protected void tick ()
    {
        final long tick = System.currentTimeMillis ();
        for ( final Test2QueryImpl query : this.queries )
        {
            query.tick ( tick );
        }
    }

    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        try
        {
            final Test2QueryImpl query = new Test2QueryImpl ( this, parameters, listener );
            this.queries.add ( query );
            return query;
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to create query", e );
            return null;
        }

    }

    public HistoricalItemInformation getInformation ()
    {
        return new HistoricalItemInformation ( "test2", new HashMap<String, Variant> () );
    }

    public void dispose ()
    {
        this.executor.shutdown ();

        final Collection<Test2QueryImpl> queries = new ArrayList<Test2QueryImpl> ( this.queries );

        for ( final Test2QueryImpl query : queries )
        {
            query.close ();
        }
    }

    protected void remove ( final Test2QueryImpl query )
    {
        this.queries.remove ( query );
    }

}
