package org.openscada.da.client.net;

import java.util.Collection;

import org.openscada.da.core.browser.Entry;

public interface FolderListener
{
    void folderChanged ( Collection<Entry> added, Collection<String> removed, boolean full );
}
