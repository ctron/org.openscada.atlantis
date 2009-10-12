package org.openscada.da.connection.provider.internal;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.connection.provider.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionServiceImpl implements ConnectionService
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionServiceImpl.class );

    private final Connection connection;

    private AutoReconnectController controller;

    private final ItemManager itemManager;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        this.connection = connection;
        if ( autoReconnectController != null )
        {
            this.controller = new AutoReconnectController ( connection, autoReconnectController );
        }
        this.itemManager = new ItemManager ( connection );
    }

    public void dispose ()
    {
        logger.info ( "Disposing: {}", this.connection );
        disconnect ();
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public AutoReconnectController getAutoReconnectController ()
    {
        return this.controller;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public void connect ()
    {
        if ( this.controller != null )
        {
            this.controller.connect ();
        }
        else
        {
            this.connection.connect ();
        }
    }

    public void disconnect ()
    {
        if ( this.controller != null )
        {
            this.controller.disconnect ();
        }
        else
        {
            this.connection.disconnect ();
        }
    }

}
