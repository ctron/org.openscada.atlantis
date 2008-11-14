package org.openscada.da.server.jdbc;

import org.openscada.da.server.jdbc.query.QueryProcessor;

public class Query
{
    private final String id;

    private final int period;

    private final String sql;

    private QueryProcessor processor;

    private final Connection connection;

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

    public void register ()
    {
        this.processor.activate ();
    }

    public void unregister ()
    {
        this.processor.deactivate ();
    }

}
