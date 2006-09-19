package org.openscada.da.core.common.configuration.xml;

import java.util.LinkedList;
import java.util.List;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.chain.ChainItem;


public class Item extends ItemBase
{
    private String _id = null;
    private DataItem _item = null;
    private List<ChainItem> _chainItems = null;

    public Item ()
    {
        super ();
    }
    
    public Item ( String id )
    {
        super ();
        _id = id;
    }
    
    public Item ( String id, Template template )
    {
        super ( template );
        _id = id;
    }
    
    public String getId ()
    {
        return _id;
    }

    public void setId ( String id )
    {
        _id = id;
    }

    public DataItem getItem ()
    {
        return _item;
    }

    public void setItem ( DataItem item )
    {
        _item = item;
    }

    public List<ChainItem> getChainItems ()
    {
        return _chainItems;
    }

    public void setChainItems ( List<ChainItem> chainItems )
    {
        _chainItems = chainItems;
    }
    
    
}
