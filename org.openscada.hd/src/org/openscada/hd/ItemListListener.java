package org.openscada.hd;

import java.util.Set;

public interface ItemListListener
{
    public void listChanged ( Set<HistoricalItemInformation> addedOrModified, Set<String> removed, boolean full );
}
