package org.openscada.da.server.common.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openscada.core.Variant;

public class FactoryTemplate
{
    private Pattern _pattern = null;
    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();
    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();
    private List<ChainEntry> _chainEntries = new LinkedList<ChainEntry> ();
    
    public FactoryTemplate ()
    {
        super ();
    }

    public FactoryTemplate ( FactoryTemplate arg0 )
    {
        super ();
        
        _pattern = arg0._pattern;
        _itemAttributes = new HashMap<String, Variant> ( arg0._itemAttributes );
        _browserAttributes = new HashMap<String, Variant> ( arg0._browserAttributes );
        _chainEntries = new LinkedList<ChainEntry> ( arg0._chainEntries );
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

    public Pattern getPattern ()
    {
        return _pattern;
    }

    public void setPattern ( Pattern pattern )
    {
        _pattern = pattern;
    }

    public List<ChainEntry> getChainEntries ()
    {
        return _chainEntries;
    }

    public void setChainEntries ( List<ChainEntry> chainEntries )
    {
        _chainEntries = chainEntries;
    }
}
