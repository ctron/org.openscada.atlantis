package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.component.Hive;

public class BaseModule
{
    private Hive _hive = null;

    private String _base = null;

    private Map<String, DataItem> _items = new HashMap<String, DataItem> ();

    public BaseModule ( Hive hive, String base )
    {
        _hive = hive;
        _base = base;
    }

    public void dispose ()
    {
        for ( DataItem item : _items.values () )
        {
            _hive.unregisterItem ( item );
            _hive.getStorage ().removed ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
        }
        _items.clear ();
    }

    protected DataItemInputChained getInput ( String name, Map<String, Variant> attributes )
    {
        String id = getItemId ( name );

        DataItem dataItem = _items.get ( name );
        if ( dataItem != null )
        {
            if ( dataItem instanceof DataItemInputChained )
            {
                return (DataItemInputChained)dataItem;
            }
            else
            {
                throw new ItemAlreadyRegisteredException ( name );
            }
        }

        DataItemInputChained item = new DataItemInputChained ( id );
        _items.put ( name, item );
        _hive.registerItem ( item );

        ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        _hive.getStorage ().added ( idesc );
        return item;
    }

    protected DataItemCommand getOutput ( String name, Map<String, Variant> attributes )
    {
        String id = getItemId ( name );

        DataItem dataItem = _items.get ( name );
        if ( dataItem != null )
        {
            if ( dataItem instanceof DataItemCommand )
            {
                return (DataItemCommand)dataItem;
            }
            else
            {
                throw new ItemAlreadyRegisteredException ( name );
            }
        }

        DataItemCommand item = new DataItemCommand ( id );
        _items.put ( name, item );
        _hive.registerItem ( item );

        ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        _hive.getStorage ().added ( idesc );
        return item;
    }
    
    private String getItemId ( String name )
    {
        return _base + "." + name;
    }
}
