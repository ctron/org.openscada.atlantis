package org.openscada.da.ice;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;

public class FolderEntry implements org.openscada.da.core.browser.FolderEntry
{
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private String _name = "";
    
    public FolderEntry ( OpenSCADA.DA.Browser.FolderEntry entry )
    {
        super ();
        _name = entry.name;
        _attributes = AttributesHelper.fromIce ( entry.attributes );
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public String getName ()
    {
        return _name;
    }
}
