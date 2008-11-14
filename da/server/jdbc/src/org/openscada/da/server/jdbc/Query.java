package org.openscada.da.server.jdbc;

import org.openscada.da.server.jdbc.query.QueryProcessor;
import org.openscada.utils.timing.Scheduler;

public class Query
{
    private final String id;

    private final int period;

    private final String sql;

    private QueryProcessor processor;

    private final Connection connection;

    private Scheduler.Job job;

    private Scheduler scheduler;

    public Query ( final String id, final int period, final String sql, final Connection connection )
    {
        super ();
        this.id = id;
        this.period = period;
        this.sql = sql;
        this.connection = connection;

        init ();
    }

    private void init ()
    {
        try
        {
            this.processor = new QueryProcessor ( this.connection, this.sql );
        }
        catch ( final Throwable e )
        {
        }
    }

    public void register ( final Scheduler scheduler )
    {
        this.processor.activate ();

        this.scheduler = scheduler;
        this.job = scheduler.scheduleJob ( new Runnable () {

            @Override
            public void run ()
            {
                Query.this.tick ();
            }
        }, this.period );
    }

    public void unregister ()
    {
        this.processor.deactivate ();

        this.scheduler.removeJob ( this.job );
        this.scheduler = null;
    }

    public void tick ()
    {
        // doQuery ();
    }

    private void doQuery () throws Exception
    {
        this.processor.doQuery ();
    }

}
