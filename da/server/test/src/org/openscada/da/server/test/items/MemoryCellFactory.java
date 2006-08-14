package org.openscada.da.server.test.items;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemFactory;
import org.openscada.da.server.test.Hive;

public class MemoryCellFactory implements DataItemFactory
{
    private Hive _hive = null;
    
    public MemoryCellFactory ( Hive hive )
    {
        _hive = hive;
    }
    
    public boolean canCreate ( String id )
    {
        return id.matches ( "memory\\.[a-z0-9]+" );
    }

    public DataItem create ( String id )
    {
        FactoryMemoryCell item = new FactoryMemoryCell ( _hive, id );
        _hive.addMemoryFactoryItem ( item );
        return item;
    }

}
