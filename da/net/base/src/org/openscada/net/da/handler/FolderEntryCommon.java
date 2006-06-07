/**
 * 
 */
package org.openscada.net.da.handler;

import java.util.Map;

import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.data.Variant;

public class FolderEntryCommon extends EntryCommon implements FolderEntry
{
    public FolderEntryCommon ( String name, Map<String, Variant> attributes )
    {
        super ( name, attributes );
    }
    
}