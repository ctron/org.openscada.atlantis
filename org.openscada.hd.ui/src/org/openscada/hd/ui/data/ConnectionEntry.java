package org.openscada.hd.ui.data;

import org.openscada.core.ConnectionInformation;
import org.openscada.hd.client.Connection;

public class ConnectionEntry
{
    private final ConnectionInformation information;

    private final Connection connection;

    public ConnectionEntry ( final ConnectionInformation information, final Connection connection )
    {
        super ();
        this.information = information;
        this.connection = connection;
    }

    public ConnectionInformation getInformation ()
    {
        return this.information;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }
}
