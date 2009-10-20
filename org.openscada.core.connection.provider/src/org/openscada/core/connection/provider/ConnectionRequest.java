package org.openscada.core.connection.provider;

import org.openscada.core.ConnectionInformation;
import org.openscada.utils.lang.Immutable;

@Immutable
public class ConnectionRequest
{
    private final String requestId;

    private final ConnectionInformation connectionInformation;

    private final Integer autoReconnectDelay;

    private final boolean initialOpen;

    public ConnectionRequest ( final String requestId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        this.requestId = requestId;
        this.connectionInformation = connectionInformation;
        this.autoReconnectDelay = autoReconnectDelay;
        this.initialOpen = initialOpen;
    }

    public boolean isPrivateRequest ()
    {
        return this.requestId != null;
    }

    public boolean isInitialOpen ()
    {
        return this.initialOpen;
    }

    public Integer getAutoReconnectDelay ()
    {
        return this.autoReconnectDelay;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public String getRequestId ()
    {
        return this.requestId;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s -> %s (Auto: %s, Open: %s, Private: %s)", this.requestId, this.connectionInformation, this.autoReconnectDelay, this.initialOpen, isPrivateRequest () );
    }
}
