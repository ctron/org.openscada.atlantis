package org.openscada.ae.ui.connection.data;

import org.openscada.ae.BrowserEntry;
import org.openscada.ae.connection.provider.ConnectionService;

public class BrowserEntryBean
{
    private final BrowserEntry entry;

    private final ConnectionService connection;

    public BrowserEntryBean ( final ConnectionService connection, final BrowserEntry entry )
    {
        this.connection = connection;
        this.entry = entry;
    }

    public ConnectionService getConnection ()
    {
        return this.connection;
    }

    public BrowserEntry getEntry ()
    {
        return this.entry;
    }

    public void dispose ()
    {
    }
}
