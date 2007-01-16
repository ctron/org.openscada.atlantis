package org.openscada.da.server.common.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.ChainProcessEntry;

public class DataItemFactoryRequest
{
    private String _id = null;
    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();
    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();
    private List<ChainProcessEntry> _itemChain = new LinkedList<ChainProcessEntry> ();
    
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
    public List<ChainProcessEntry> getItemChain ()
    {
        return _itemChain;
    }
    public void setItemChain ( List<ChainProcessEntry> itemChain )
    {
        _itemChain = itemChain;
    }
    
}
