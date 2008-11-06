package org.openscada.da.server.jdbc;

import org.openscada.da.server.jdbc.query.QueryProcessor;

public class Query
{
    private final String id;

    private final int period;

    private final String connectionClass;

    private final String uri;

    private final String sql;

    private QueryProcessor processor;

    public Query ( final String id, final int period, final String connectionClass, final String uri, final String sql )
    {
        super ();
        this.id = id;
        this.period = period;
        this.connectionClass = connectionClass;
        this.uri = uri;
        this.sql = sql;

        init ();
    }

    private void init ()
    {
        try
        {
            this.processor = new QueryProcessor ( this.uri, this.sql );
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
