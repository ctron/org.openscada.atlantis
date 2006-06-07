package org.openscada.net.da.handler;

import java.util.Map;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.data.Variant;

public class EntryCommon implements Entry
{
    private String _name;
    private Map<String, Variant> _attributes;
    
    public EntryCommon ( String name, Map<String, Variant> attributes )
    {
        _name = name;
        _attributes = attributes;
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public String getName ()
    {
        return _name;
    }

}
