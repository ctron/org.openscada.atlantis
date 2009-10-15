package org.openscada.hd.connection.provider.internal;

import org.openscada.core.connection.provider.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionProvider;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider extends AbstractConnectionProvider
{
    @SuppressWarnings ( "unused" )
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionProvider.class );

    public ConnectionProvider ( final BundleContext context )
    {
        super ( context, "hd" );
    }

    @Override
    protected AbstractConnectionManager createConnectionManager ( final ConnectionRequest request )
    {
        return new ConnectionManager ( this.context, request.getRequestId (), request.getConnectionInformation (), request.getAutoReconnectDelay (), request.isInitialOpen () );
    }
}
