package org.openscada.da.server.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.ItemStorage;
import org.openscada.da.server.common.DataItem;

public class Loader
{
    protected String _itemPrefix;
    protected Hive _hive;
    protected Collection<ItemStorage> _storages;

    public void setHive ( Hive hive )
    {
        this._hive = hive;
    }
    
    public void setStorages ( Collection<ItemStorage> storages )
    {
        this._storages = storages;
    }
    
    public void setItemPrefix ( String itemPrefix )
    {
        _itemPrefix = itemPrefix;
    }
    
    public void injectItem ( DataItem item )
    {
        _hive.registerItem ( item );
        for ( ItemStorage storage : _storages )
        {
            storage.added ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
        }
    }
    
    public void injectItem ( DataItem item, Map<String, Variant> attributes )
    {
        _hive.registerItem ( item );
        for ( ItemStorage storage : _storages )
        {
            storage.added ( new ItemDescriptor ( item, attributes ) );
        }
    }

}
