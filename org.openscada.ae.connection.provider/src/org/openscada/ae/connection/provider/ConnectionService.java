package org.openscada.ae.connection.provider;

import org.openscada.ae.client.Connection;

public interface ConnectionService extends org.openscada.core.connection.provider.ConnectionService
{
    public Connection getConnection ();
}
