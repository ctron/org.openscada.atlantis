package org.openscada.da.core.browser;

import java.util.Collection;


public interface FolderListener
{
    void folderChanged ( Location location, Collection<Entry> added, Collection<String> removed, boolean full );
}
