package org.openscada.da.server.jdbc;

import java.util.Collection;
import java.util.LinkedList;

public class Connection
{
    private final Collection<Query> queries = new LinkedList<Query> ();

    private Throwable globalError = null;

    private final String username;

    private final String password;

    private final String uri;

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

    public void register ()
    {
        for ( final Query query : this.queries )
        {
            query.register ();
        }
    }

    public void unregister ()
    {
        for ( final Query query : this.queries )
        {
            query.unregister ();
        }
    }
}
