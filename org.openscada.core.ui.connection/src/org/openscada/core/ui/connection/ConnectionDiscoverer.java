package org.openscada.core.ui.connection;

import org.eclipse.ui.services.IDisposable;

public interface ConnectionDiscoverer extends IDisposable
{
    public void addConnectionListener ( ConnectionDiscoveryListener listener );

    public void removeConnectionListener ( ConnectionDiscoveryListener listener );
}
