package org.openscada.da.core.browser.common;

import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.common.DataItem;

public class DataItemEntryCommon implements DataItemEntry
{

    private String _name = null;
    private DataItem _item = null;
    
    public DataItemEntryCommon ( String name, DataItem item )
    {
        _name = name;
        _item = item;
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

}
