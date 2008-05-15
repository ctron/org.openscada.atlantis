package org.openscada.da.server.opc2.configuration;

import java.util.Set;

public interface ItemSourceListener
{
    /**
     * Notifies the item source listener that the set of available items has changed
     * @param availableItems the new set of available items
     */
    public void availableItemsChanged ( Set<ItemDescription> availableItems );
}
