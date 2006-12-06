package org.openscada.da.server.opc;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.query.ItemDescriptor;

public class OPCItemDescription
{
    private OPCItem _item = null;
    private ItemDescriptor _itemDescriptor = null;
    
    public OPCItemDescription ( OPCItem item, Map<String,Variant> descriptor )
    {
        super ();
        _item = item;
        _itemDescriptor = new ItemDescriptor ( item, descriptor );
    }
    
    public OPCItem getItem ()
    {
        return _item;
    }
    
    public ItemDescriptor getItemDescriptor ()
    {
        return _itemDescriptor;
    }
}
