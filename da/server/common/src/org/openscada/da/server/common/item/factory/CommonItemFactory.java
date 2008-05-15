package org.openscada.da.server.common.item.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;

/**
 * This item factory only creates the items but does not register them anywhere
 * @author Jens Reimann
 *
 */
public class CommonItemFactory implements ItemFactory
{
    private static final String DEFAULT_ID_DELIMITER = ".";

    private String baseId = null;
    private String idDelimiter = DEFAULT_ID_DELIMITER;

    private Map<String, DataItem> itemMap = new HashMap<String, DataItem> ();
    private Set<ItemFactory> factoryMap = new HashSet<ItemFactory> ();
    
    private boolean disposed = false;

    private ItemFactory parentItemFactory;

    public CommonItemFactory ()
    {
        this ( null, null, DEFAULT_ID_DELIMITER );
    }

    public CommonItemFactory ( ItemFactory parentItemFactory, String baseId, String idDelimiter )
    {
        this.parentItemFactory = parentItemFactory;
        if ( parentItemFactory != null )
        {
            parentItemFactory.addSubFactory ( this );
        }
        
        this.baseId = baseId;
        this.idDelimiter = idDelimiter;

        if ( this.idDelimiter == null )
        {
            this.idDelimiter = DEFAULT_ID_DELIMITER;
        }
    }
    
    public boolean isDisposed ()
    {
        return this.disposed;
    }

    /**
     * Generate a global Id by using the base id and the local id
     * @param localId the local id
     * @return the global id
     */
    protected String generateId ( String localId )
    {
        if ( this.baseId == null )
        {
            return localId;
        }
        else
        {
            return this.baseId + idDelimiter + localId;
        }
    }
    
    private void registerItem ( DataItem newItem )
    {
        DataItem oldItem = this.itemMap.put ( newItem.getInformation ().getName (), newItem );
        if ( oldItem != null )
        {
            disposeItem ( oldItem );
        }
    }

    protected DataItemCommand constructCommand ( String localId )
    {
        final DataItemCommand commandItem = new DataItemCommand ( generateId ( localId ) );
        registerItem ( commandItem );
        return commandItem;
    }

    protected DataItemInputChained constructInput ( String localId )
    {
        final DataItemInputChained inputItem = new DataItemInputChained ( generateId ( localId ) );
        registerItem ( inputItem );
        return inputItem;
    }

    protected WriteHandlerItem constructInputOutput ( String localId, WriteHandler writeHandler )
    {
        final WriteHandlerItem ioItem = new WriteHandlerItem ( localId, writeHandler );
        registerItem ( ioItem );
        return ioItem;
    }

    public void dispose ()
    {
        if ( isDisposed () )
        {
            return;
        }
        
        this.disposed = true;
        
        if ( this.parentItemFactory != null )
        {
            this.parentItemFactory.removeSubFactory ( this );
        }
    
        Collection<DataItem> items = new ArrayList<DataItem> ( this.itemMap.values () );
        for ( DataItem item : items )
        {
            disposeItem ( item );
        }
        // should be clear anyways
        this.itemMap.clear ();
        
        for ( ItemFactory factory : this.factoryMap )
        {
            factory.dispose ();
        }
    }

    public boolean disposeItem ( DataItem item )
    {
        final DataItem removedItem = this.itemMap.remove ( item.getInformation ().getName () );
        return removedItem != null;
    }

    public DataItemCommand createCommand ( String localId )
    {
        return constructCommand ( localId );
    }

    public DataItemInputChained createInput ( String localId )
    {
        return constructInput ( localId );
    }

    public WriteHandlerItem createInputOutput ( String localId, WriteHandler writeHandler )
    {
        return constructInputOutput ( localId, writeHandler );
    }
    
    public String getBaseId ()
    {
        return this.baseId;
    }

    public boolean addSubFactory ( ItemFactory itemFactory )
    {
        return factoryMap.add ( itemFactory );
    }

    public boolean removeSubFactory ( ItemFactory itemFactory )
    {
        return factoryMap.remove ( itemFactory );
    }
}
