package org.openscada.core.connection.provider;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionService implements org.openscada.core.connection.provider.ConnectionService
{
    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionService.class );

    private final Connection connection;

    private final AutoReconnectController controller;

    public AbstractConnectionService ( final Connection connection, final Integer autoReconnectController )
    {
        super ();
        this.connection = connection;

        if ( autoReconnectController != null )
        {
            this.controller = new AutoReconnectController ( connection, autoReconnectController );
        }
        else
        {
            this.controller = null;
        }
    }

    public void dispose ()
    {
        logger.info ( "Disposing: {}", this.connection );
        disconnect ();
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