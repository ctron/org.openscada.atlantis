package org.openscada.da.client.ice;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;

public class DriverAdapter implements org.openscada.da.client.connector.DriverAdapter
{
    public Connection createConnection ( final ConnectionInformation connectionInformation )
    {
        return new DriverInformation ().create ( connectionInformation );
    }

}
