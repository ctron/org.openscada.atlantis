package org.openscada.da.net.client;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.openscada.da.client.net.DriverInformation;

public class DriverAdapter implements org.openscada.da.client.connector.DriverAdapter
{
    public Connection createConnection ( final ConnectionInformation connectionInformation )
    {
        return new DriverInformation ().create ( connectionInformation );
    }

}
