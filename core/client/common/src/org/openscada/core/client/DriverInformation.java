package org.openscada.core.client;

public interface DriverInformation
{
    public abstract Class getConnectionClass ();
    public abstract Connection create ( ConnectionInformation connectionInformation );
}
