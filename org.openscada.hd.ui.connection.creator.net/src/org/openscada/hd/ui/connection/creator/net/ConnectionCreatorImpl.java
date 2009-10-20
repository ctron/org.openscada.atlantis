package org.openscada.hd.ui.connection.creator.net;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.DriverInformation;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.creator.ConnectionCreator;
import org.openscada.hd.client.Connection;
import org.openscada.hd.client.net.DriverFactoryImpl;
import org.openscada.hd.connection.provider.ConnectionServiceImpl;

public class ConnectionCreatorImpl implements ConnectionCreator
{
    public ConnectionService createConnection ( final ConnectionInformation connectionInformation, final Integer autoReconnectDelay )
    {
        final DriverInformation di = new DriverFactoryImpl ().getDriverInformation ( connectionInformation );
        final Connection c = (Connection)di.create ( connectionInformation );
        return new ConnectionServiceImpl ( c, autoReconnectDelay );
    }
}
