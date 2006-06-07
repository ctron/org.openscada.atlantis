package org.openscada.da.core.browser.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class DataItemEntryCommon implements DataItemEntry
{

    private String _name = null;
    private DataItem _item = null;
    private Map < String, Variant > _attributes = null;
    
    public DataItemEntryCommon ( String name, DataItem item, Map < String, Variant > attributes )
    {
        _name = name;
        _item = item;
        _attributes = attributes;
    }
    
    public String getId ()
    {
        return _item.getInformation ().getName ();
    }

    public String getName ()
    {
        return _name;
    }
    
    public DataItem getItem ()
    {
        return _item;
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }
    
    public EnumSet<IODirection> getIODirections ()
    {
        return _item.getInformation ().getIODirection ().clone ();
    }

}
