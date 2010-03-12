package org.openscada.core.connection.provider;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.Connection;

public interface ConnectionService
{
    public Connection getConnection ();

    public AutoReconnectController getAutoReconnectController ();

    public void connect ();

    public void disconnect ();

    public static final String CONNECTION_URI = "connection.uri";

    public Class<?>[] getSupportedInterfaces ();

    public void dispose ();
}
