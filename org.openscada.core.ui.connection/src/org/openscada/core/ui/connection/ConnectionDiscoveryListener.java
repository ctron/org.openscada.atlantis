package org.openscada.core.ui.connection;

import org.openscada.core.ConnectionInformation;

public interface ConnectionDiscoveryListener
{
    public void discoveryUpdate ( ConnectionInformation[] added, ConnectionInformation[] removed );
}
