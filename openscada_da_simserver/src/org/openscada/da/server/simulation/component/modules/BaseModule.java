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

    private final Map<String, DataItem> _items = new HashMap<String, DataItem> ();

    public BaseModule ( final Hive hive, final String base )
    {
        this._hive = hive;
        this._base = base;
    }

    public void dispose ()
    {
        for ( final DataItem item : this._items.values () )
        {
            this._hive.unregisterItem ( item );
            this._hive.getStorage ().removed ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
        }
        this._items.clear ();
    }

    protected DataItemInputChained getInput ( final String name, final Map<String, Variant> attributes )
    {
        final String id = getItemId ( name );

        final DataItem dataItem = this._items.get ( name );
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

        final DataItemInputChained item = new DataItemInputChained ( id );
        this._items.put ( name, item );
        this._hive.registerItem ( item );

        final ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        this._hive.getStorage ().added ( idesc );
        return item;
    }

    protected DataItemCommand getOutput ( final String name, final Map<String, Variant> attributes )
    {
        final String id = getItemId ( name );

        final DataItem dataItem = this._items.get ( name );
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

        final DataItemCommand item = new DataItemCommand ( id );
        this._items.put ( name, item );
        this._hive.registerItem ( item );

        final ItemDescriptor idesc = new ItemDescriptor ( item, attributes );
        this._hive.getStorage ().added ( idesc );
        return item;
    }

    private String getItemId ( final String name )
    {
        return this._base + "." + name;
    }
}
