/**
 * 
 */
package org.openscada.net.da.handler;

import org.openscada.da.core.browser.FolderEntry;

public class FolderEntryCommon implements FolderEntry
{
    private String _name = "";
    
    public FolderEntryCommon ( String name )
    {
        _name = name;
    }
    
    public String getName ()
    {
        return _name;
    }
}