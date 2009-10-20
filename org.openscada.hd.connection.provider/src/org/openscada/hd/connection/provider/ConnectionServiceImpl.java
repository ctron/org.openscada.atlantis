package org.openscada.hd.connection.provider;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.hd.client.Connection;

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

    public Class<?>[] getSupportedInterfaces ()
    {
        return new Class<?>[] { org.openscada.core.connection.provider.ConnectionService.class, ConnectionService.class };
    }

}
