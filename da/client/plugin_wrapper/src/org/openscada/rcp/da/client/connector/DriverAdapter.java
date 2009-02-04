package org.openscada.rcp.da.client.connector;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;

public interface DriverAdapter
{
    public Connection createConnection ( ConnectionInformation connectionInformation );
}
