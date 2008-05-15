package org.openscada.da.server.common.item.factory;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.impl.HiveCommon;

/**
 * This item factory creates the items and registers them in the hive
 * @author jens
 *
 */
public class HiveItemFactory extends CommonItemFactory
{
    private HiveCommon hive;

    public HiveItemFactory ( HiveCommon hive )
    {
        this.hive = hive;
    }
    
    public HiveItemFactory ( ItemFactory parentItemFactory, HiveCommon hive, String baseId, String idDelimiter )
    {
        super ( parentItemFactory, baseId, idDelimiter );
        this.hive = hive;
    }

    @Override
    protected DataItemCommand constructCommand ( String localId )
    {
        DataItemCommand item = super.constructCommand ( localId );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    protected DataItemInputChained constructInput ( String localId )
    {
        DataItemInputChained item = super.constructInput ( localId );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    protected WriteHandlerItem constructInputOutput ( String localId, WriteHandler writeHandler )
    {
        WriteHandlerItem item = super.constructInputOutput ( localId, writeHandler );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    public boolean disposeItem ( DataItem item )
    {
        if ( super.disposeItem ( item ) )
        {
            this.hive.unregisterItem ( item );
            return true;
        }
        else
        {
            return false;
        }
    }
}
