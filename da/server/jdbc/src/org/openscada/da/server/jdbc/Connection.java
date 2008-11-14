package org.openscada.da.server.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import org.openscada.utils.timing.Scheduler;

public class Connection
{
    private final Collection<Query> queries = new LinkedList<Query> ();

    private Throwable globalError = null;

    private final String username;

    private final String password;

    private final String uri;

    private java.sql.Connection connection;

    public Connection ( final String connectionClass, final String uri, final String username, final String password )
    {
        this.uri = uri;
        this.username = username;
        this.password = password;
        try
        {
            if ( connectionClass != null )
            {
                Class.forName ( connectionClass );
            }
        }
        catch ( final Throwable e )
        {
            this.globalError = e;
        }
    }

    public void add ( final Query query )
    {
        this.queries.add ( query );
    }

    public void register ( final Hive hive, final Scheduler scheduler )
    {
        for ( final Query query : this.queries )
        {
            query.register ( scheduler );
        }
    }

    public void unregister ( final Hive hive )
    {
        for ( final Query query : this.queries )
        {
            query.unregister ();
        }
    }

    protected java.sql.Connection createConnection () throws SQLException
    {
        return DriverManager.getConnection ( this.uri, this.username, this.password );
    }

    public java.sql.Connection getConnection () throws SQLException
    {
        if ( this.connection == null )
        {
            this.connection = createConnection ();
        }

        return this.connection;
    }
}
