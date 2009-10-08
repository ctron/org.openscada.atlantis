package org.openscada.da.client.connection.service.internal;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.connection.service.ConnectionService;

public class ConnectionServiceImpl implements ConnectionService
{
    private final Connection connection;

    private final ItemManager itemManager;

    private final AutoReconnectController controller;

    public ConnectionServiceImpl ( final Connection connection, final ItemManager itemManager )
    {
        this.connection = connection;
        this.itemManager = itemManager;
        this.controller = new AutoReconnectController ( connection );
        this.controller.connect ();
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public void dispose ()
    {
        this.controller.disconnect ();
    }

}
