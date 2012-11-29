package org.openscada.ae.server.ngp;

import java.util.List;
import java.util.Set;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.data.BrowserEntry;

public class BrowserListenerManager implements BrowserListener
{

    private volatile boolean disposed;

    private final ServerConnectionImpl connection;

    public BrowserListenerManager ( final ServerConnectionImpl connection )
    {
        this.connection = connection;
    }

    @Override
    public void dataChanged ( final List<BrowserEntry> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        this.connection.handleBrowseDataChanged ( this, addedOrUpdated, removed, full );
    }

}
