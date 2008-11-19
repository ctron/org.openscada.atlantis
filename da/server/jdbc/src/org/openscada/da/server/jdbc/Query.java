package org.openscada.da.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.jdbc.query.QueryProcessor;
import org.openscada.utils.timing.Scheduler;

public class Query
{
    private static Logger logger = Logger.getLogger ( Query.class );

    private final String id;

    private final int period;

    private final String sql;

    private QueryProcessor processor;

    private final Connection connection;

    private Scheduler.Job job;

    private Scheduler scheduler;

    private String[] fields = new String[] {};

    public Query ( final String id, final int period, final String sql, final Connection connection )
    {
        super ();
        this.id = id;
        this.period = period;
        this.sql = sql;
        this.connection = connection;

        try
        {
            this.processor = new QueryProcessor ( this.connection, this.sql );
        }
        catch ( final Throwable e )
        {
            setError ( e );
        }

        logger.info ( "Created new query: " + this.id );

    }

    public void register ( final Scheduler scheduler )
    {
        this.processor.activate ();

        this.scheduler = scheduler;
        this.job = scheduler.scheduleJob ( new Runnable () {

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
        try
        {
            if ( this.processor != null )
            {
                doQuery ();
            }
        }
        catch ( Throwable e )
        {
            setError ( e );
        }
    }

    private void setError ( Throwable e )
    {
        logger.error ( "Failed to query", e );
        // TODO Auto-generated method stub

    }

    private void doQuery () throws Exception
    {
        java.sql.Connection connection = this.connection.getConnection ();
        try
        {
            PreparedStatement stmt = connection.prepareStatement ( this.sql );
            try
            {
                ResultSet result = stmt.executeQuery ();
                if ( result.next () )
                {
                    for ( String field : this.fields )
                    {
                        updateField ( field, result );
                    }
                }
                result.close ();
            }
            finally
            {
                stmt.close ();
            }
        }
        finally
        {
            if ( connection != null )
            {
                connection.close ();
            }
        }
    }

    private void updateField ( String key, ResultSet result )
    {
        try
        {
            setValue ( key, new Variant ( result.getObject ( key ) ) );
        }
        catch ( Throwable e )
        {
            setError ( key, e );
        }
    }

    private DataItemInputChained getItem ( String key )
    {
        return null;
    }

    private void setValue ( String key, Variant value )
    {
        // TODO Auto-generated method stub
        logger.info ( "Setting value: " + key + "=" + value );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", null );

        getItem ( key ).updateData ( value, attributes, AttributeMode.UPDATE );
    }

    private void setError ( String key, Throwable e )
    {
        logger.info ( "Setting error: " + key + " = " + e.getMessage () );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", new Variant ( true ) );
        attributes.put ( "jdbc.error.message", new Variant ( e.getMessage () ) );

        getItem ( key ).updateData ( null, attributes, AttributeMode.UPDATE );
    }

}
