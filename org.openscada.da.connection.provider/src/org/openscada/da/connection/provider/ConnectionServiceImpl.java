package org.openscada.da.connection.provider;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final ItemManager itemManager;

    private final FolderManager folderManager;

    private final Connection connection;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        super ( connection, autoReconnectController );
        this.connection = connection;
        this.itemManager = new ItemManager ( connection );
        this.folderManager = new FolderManager ( connection );
    }

    @Override
    public org.openscada.da.client.Connection getConnection ()
    {
        return this.connection;
    }

    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public Class<?>[] getSupportedInterfaces ()
    {
        return new Class<?>[] { org.openscada.core.connection.provider.ConnectionService.class, ConnectionService.class };
    }

}
