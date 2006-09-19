package org.openscada.da.core.common.configuration.xml;

import org.openscada.da.core.common.DataItem;


public class Item extends ItemBase
{
    private String _id = null;
    private DataItem _item = null;

    public Item ()
    {
        super ();
    }
    
    public Item ( Item arg0 )
    {
        super ( arg0 );
        
        _id = arg0._id;
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
    
    
}
