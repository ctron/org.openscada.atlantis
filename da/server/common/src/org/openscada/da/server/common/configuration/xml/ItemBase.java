package org.openscada.da.server.common.configuration.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.factory.ChainEntry;

public class ItemBase
{

    private Factory _factory = null;
    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();
    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();
    private List<ChainEntry> _chainEntries = new LinkedList<ChainEntry> ();

    public ItemBase ()
    {
        super ();
    }

    public ItemBase ( ItemBase arg0 )
    {
        super ();
        
        _factory = arg0._factory;
        _chainEntries = new LinkedList<ChainEntry> ( arg0._chainEntries );
        
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

    public List<ChainEntry> getChainEntries ()
    {
        return _chainEntries;
    }

    public void setChainEntries ( List<ChainEntry> chainItems )
    {
        _chainEntries = chainItems;
    }

}