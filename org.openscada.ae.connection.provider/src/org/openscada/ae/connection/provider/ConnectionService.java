package org.openscada.ae.connection.provider;

import org.openscada.ae.client.Connection;
import org.openscada.ae.client.EventManager;
import org.openscada.ae.client.MonitorManager;

public interface ConnectionService extends org.openscada.core.connection.provider.ConnectionService
{
    public EventManager getEventManager ();

    public MonitorManager getMonitorManager ();

    public Connection getConnection ();
}
