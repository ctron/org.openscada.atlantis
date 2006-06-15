package org.openscada.da.core.browser.common.query;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class ItemDescriptor
{
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();
    private DataItem _item = null;
    
    public ItemDescriptor ( DataItem item, Map<String, Variant> attributes )
    {
        _item = item;
        _attributes = attributes;
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public DataItem getItem ()
    {
        return _item;
    }
}
