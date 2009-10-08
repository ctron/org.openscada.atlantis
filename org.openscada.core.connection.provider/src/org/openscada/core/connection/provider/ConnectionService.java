package org.openscada.core.connection.provider;

import java.util.Set;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.Connection;

public interface ConnectionService
{
    public Connection getConnection ();

    public AutoReconnectController getAutoReconnectController ();

    public Set<ConnectionRequest> getRequests ();
}
