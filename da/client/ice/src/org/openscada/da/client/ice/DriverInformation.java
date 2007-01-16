package org.openscada.da.client.ice;

import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionInformation;

public class DriverInformation implements org.openscada.core.client.DriverInformation
{

    public Connection create ( ConnectionInformation connectionInformation )
    {
        return new org.openscada.da.client.ice.Connection ( connectionInformation );
    }

    public Class getConnectionClass ()
    {
        return org.openscada.da.client.ice.Connection.class;
    }

}
