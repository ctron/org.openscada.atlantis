package org.openscada.hd.connection.provider;

import org.openscada.hd.client.Connection;

public interface ConnectionService extends org.openscada.core.connection.provider.ConnectionService
{
    public Connection getConnection ();
}
