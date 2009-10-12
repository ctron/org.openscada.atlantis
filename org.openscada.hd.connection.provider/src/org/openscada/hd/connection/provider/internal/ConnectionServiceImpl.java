package org.openscada.hd.connection.provider.internal;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.hd.client.Connection;
import org.openscada.hd.connection.provider.ConnectionService;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final Connection connection;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        super ( connection, autoReconnectController );
        this.connection = connection;
    }

    @Override
    public org.openscada.hd.client.Connection getConnection ()
    {
        return this.connection;
    }

}
