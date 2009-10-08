package org.openscada.core.ui.connection;

public interface ConnectionDiscoverer
{
    public void addConnectionListener ( ConnectionDiscoveryListener listener );

    public void removeConnectionListener ( ConnectionDiscoveryListener listener );
}
