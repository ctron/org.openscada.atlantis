package org.openscada.hd.connection.provider.internal;

import org.openscada.core.connection.provider.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionProvider;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.osgi.framework.BundleContext;

public class ConnectionProvider extends AbstractConnectionProvider
{
    public ConnectionProvider ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected AbstractConnectionManager createConnectionManager ( final ConnectionRequest request )
    {
        return new ConnectionManager ( this.context, null, request.getConnectionInformation (), request.getAutoReconnectDelay (), request.isInitialOpen () );
    }
}
