package org.openscada.da.server.test.items;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemOutput;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.test.Hive;

public class MemoryCellItem extends DataItemOutput
{
    private Hive _hive;
    
    private Map<Integer,MemoryDataItem> _items = new HashMap<Integer,MemoryDataItem>();
    private AttributeManager _attributes = null;
    
    public MemoryCellItem ( Hive hive, String name )
    {
        super ( name );
        _hive = hive;
        
        _attributes = new AttributeManager ( this );
        
        updateCells ( 0 );
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes.getCopy();
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        // no op
    }

    public void setValue ( Variant value ) throws InvalidOperationException,
            NullValueException, NotConvertableException
    {
        int num = value.asInteger ();
        
        updateCells ( num );
    }
    
    private void setSizeAttribute ( int num )
    {
        _attributes.update ( "size", new Variant(num) );
    }

    private void updateCells ( int num )
    {
        if ( num < 0 )
            num = 0;
        
        synchronized ( _items )
        {
            Map<Integer,MemoryDataItem> newItems = new HashMap<Integer,MemoryDataItem> ( num );
            
            int pos;
            for ( pos = 0; pos < num && pos < _items.size (); pos++ )
            {
                if ( _items.containsKey ( pos ) )
                {
                    newItems.put ( pos, _items.get ( pos ) );
                    _items.remove ( pos );
                }
            }
            
            for ( Map.Entry<Integer, MemoryDataItem> entry : _items.entrySet () )
            {
                _hive.unregisterItem ( entry.getValue () );
            }
            
            for ( int i = pos ; i < num ; i++ )
            {
                MemoryDataItem item = new MemoryDataItem ( getName() + "-" + i );
                _hive.registerItem ( item );
                newItems.put ( i, item );
            }
            
            _items = newItems;
            
            setSizeAttribute ( num );
        }
    }
}
