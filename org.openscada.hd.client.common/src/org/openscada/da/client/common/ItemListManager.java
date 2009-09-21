package org.openscada.da.client.common;

import java.util.Set;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.client.Connection;

public class ItemListManager implements ItemListListener
{
    public ItemListManager ( final Connection connection )
    {
        connection.addListListener ( this );
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
    }
}
