package org.openscada.da.ui.connection.creator.net;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.DriverInformation;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.creator.ConnectionCreator;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.DriverFactory;
import org.openscada.da.connection.provider.ConnectionServiceImpl;

public class ConnectionCreatorImpl implements ConnectionCreator
{
    public ConnectionService createConnection ( final ConnectionInformation connectionInformation, final Integer autoReconnectDelay )
    {
        final DriverInformation di = new DriverFactory ().getDriverInformation ( connectionInformation );
        final Connection c = (Connection)di.create ( connectionInformation );
        return new ConnectionServiceImpl ( c, autoReconnectDelay );
    }
}
