package org.openscada.hd.ui.connection.internal;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ui.connection.views.ConnectionWrapper;

public class ItemWrapper
{
    private final ConnectionWrapper connection;

    private final HistoricalItemInformation itemInformation;

    public ItemWrapper ( final ConnectionWrapper connection, final HistoricalItemInformation itemInformation )
    {
        this.connection = connection;
        this.itemInformation = itemInformation;
    }

    public HistoricalItemInformation getItemInformation ()
    {
        return this.itemInformation;
    }

    public ConnectionWrapper getConnection ()
    {
        return this.connection;
    }
}
