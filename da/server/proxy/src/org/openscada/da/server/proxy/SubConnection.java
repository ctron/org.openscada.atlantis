package org.openscada.da.server.proxy;

import org.openscada.da.client.Connection;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public class SubConnection
{
    private final Connection connection;

    private final String id;

    private final String prefix;

    public SubConnection ( final Connection connection, final String id, final String prefix )
    {
        super ();
        this.connection = connection;
        this.id = id;
        this.prefix = prefix;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public String getId ()
    {
        return this.id;
    }

    public String getPrefix ()
    {
        return this.prefix;
    }
}
