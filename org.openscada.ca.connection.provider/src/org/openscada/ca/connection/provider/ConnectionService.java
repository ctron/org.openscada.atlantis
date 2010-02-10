package org.openscada.ca.connection.provider;

import org.openscada.ca.client.Connection;

public interface ConnectionService extends org.openscada.core.connection.provider.ConnectionService
{
    public Connection getConnection ();
}
