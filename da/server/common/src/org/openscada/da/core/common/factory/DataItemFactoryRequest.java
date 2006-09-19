package org.openscada.da.core.common.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.common.chain.ChainItem;

public class DataItemFactoryRequest
{
    private String _id = null;
    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();
    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();
    private List<ChainItem> _itemChain = new LinkedList<ChainItem> ();
    
    public Map<String, Variant> getBrowserAttributes ()
    {
        return _browserAttributes;
    }
    public void setBrowserAttributes ( Map<String, Variant> browserAttributes )
    {
        _browserAttributes = browserAttributes;
    }
    public String getId ()
    {
        return _id;
    }
    public void setId ( String id )
    {
        _id = id;
    }
    public Map<String, Variant> getItemAttributes ()
    {
        return _itemAttributes;
    }
    public void setItemAttributes ( Map<String, Variant> itemAttributes )
    {
        _itemAttributes = itemAttributes;
    }
    public List<ChainItem> getItemChain ()
    {
        return _itemChain;
    }
    public void setItemChain ( List<ChainItem> itemChain )
    {
        _itemChain = itemChain;
    }
    
}
