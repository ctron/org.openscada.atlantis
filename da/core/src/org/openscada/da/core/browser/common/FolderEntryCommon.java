package org.openscada.da.core.browser.common;

import java.util.Map;

import org.openscada.da.core.browser.Folder;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.data.Variant;

public class FolderEntryCommon implements FolderEntry
{
    private String _name = null;
    private Folder _folder = null;
    private Map < String, Variant > _attributes = null;
    
    public FolderEntryCommon ( String name, Folder folder, Map<String, Variant> attributes )
    {
        _name = name;
        _folder = folder;
        _attributes = attributes;
    }
   
    public String getName ()
    {
        return _name;
    }

    public Folder getFolder ()
    {
        return _folder;
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }
}
