package org.openscada.da.server.common.item.factory;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;

public interface ItemFactory
{
    public abstract DataItemCommand createCommand ( String localId );
    public abstract DataItemInputChained createInput ( String localId );
    public abstract WriteHandlerItem createInputOutput ( String localId, WriteHandler writeHandler );
    
    /**
     * Dispose a data item
     * @param item a data item created by this data item factory
     */
    public abstract void disposeItem ( DataItem item );
    
    /**
     * Dispose all items that where created by this factory and where not disposed up to now
     */
    public abstract void dispose ();
    
    /**
     * Dispose all items at once
     */
    public abstract void disposeAllItems ();
    
    /**
     * Add a factory that will get disposed when this factory gets disposed
     * @param itemFactory the item factory to add
     */
    public abstract boolean addSubFactory ( ItemFactory itemFactory );
    
    /**
     * Remove a factory from the dispose list that was added to this factory
     * using {@link #addSubFactory(ItemFactory)}
     * @param itemFactory
     */
    public abstract boolean removeSubFactory ( ItemFactory itemFactory );
    
    public abstract String getBaseId ();
}
