package org.openscada.core.client;

public interface DriverFactory
{
    public abstract DriverInformation getDriverInformation ( ConnectionInformation connectionInformation );
}
