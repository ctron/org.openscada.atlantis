package org.openscada.da.core.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.data.AttributesHelper;
import org.openscada.da.core.data.Variant;

public class AttributeManager
{
    private DataItemBase _item = null;
    private Map < String, Variant > _attributes = null;
    
    public AttributeManager ( DataItemBase item )
    {
        _item = item;
        _attributes = new HashMap < String, Variant > ();
    }
    
    public Map < String, Variant > getCopy ()
    {
        synchronized ( _attributes )
        {
            return new HashMap < String, Variant > ( _attributes );
        }
    }
    
    public Map < String, Variant > get ()
    {
        synchronized ( _attributes )
        {
            return _attributes;
        }
    }
    
    public void update ( Map < String, Variant > updates )
    {
        Map<String, Variant> diff = new HashMap<String, Variant>();
        synchronized ( _attributes )
        {
            AttributesHelper.mergeAttributes ( _attributes, updates, diff );
            _item.notifyAttributes ( diff );
        }
    }
    
    public void update ( String name, Variant value )
    {
        Map<String, Variant> updates = new HashMap<String,Variant>();
        updates.put ( name, value );
        
        update ( updates );
    }
}
