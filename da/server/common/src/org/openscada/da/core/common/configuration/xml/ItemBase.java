package org.openscada.da.core.common.configuration.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;

public class ItemBase
{

    private Factory _factory = null;
    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();
    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    public ItemBase ()
    {
        super ();
    }

    public ItemBase ( ItemBase arg0 )
    {
        super ();
        
        _factory = arg0._factory;
        
        _itemAttributes = new HashMap<String, Variant> ( arg0._itemAttributes );
        _browserAttributes = new HashMap<String, Variant> ( arg0._browserAttributes );
    }
    
    public Map<String, Variant> getBrowserAttributes ()
    {
        return _browserAttributes;
    }

    public void setBrowserAttributes ( Map<String, Variant> browserAttributes )
    {
        _browserAttributes = browserAttributes;
    }

    public Map<String, Variant> getItemAttributes ()
    {
        return _itemAttributes;
    }

    public void setItemAttributes ( Map<String, Variant> itemAttributes )
    {
        _itemAttributes = itemAttributes;
    }

    public Factory getFactory ()
    {
        return _factory;
    }

    public void setFactory ( Factory factory )
    {
        _factory = factory;
    }

}