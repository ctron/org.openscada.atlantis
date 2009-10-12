package org.openscada.da.connection.provider.internal;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.connection.provider.ConnectionService;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final ItemManager itemManager;

    private final Connection connection;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        super ( connection, autoReconnectController );
        this.connection = connection;
        this.itemManager = new ItemManager ( connection );
    }

    @Override
    public org.openscada.da.client.Connection getConnection ()
    {
        return this.connection;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

}
