package org.openscada.da.core.browser.common;

import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.common.impl.Folder;

public class FolderEntryCommon implements FolderEntry
{
    private String _name = null;
    private Folder _folder = null;
    
    public FolderEntryCommon ( String name, Folder folder )
    {
        _name = name;
        _folder = folder;
    }
    
    public String getName ()
    {
        return _name;
    }

    public Folder getFolder ()
    {
        return _folder;
    }
}
