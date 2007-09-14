package org.openscada.da.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;

public class Entry
{
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    public Entry ()
    {
        super ();
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        _attributes = attributes;
    }
}