package org.openscada.core.ui.connection.creator;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionService;

public interface ConnectionCreator
{
    public ConnectionService createConnection ( ConnectionInformation connectionInformation, Integer autoReconnectDelay );
}
