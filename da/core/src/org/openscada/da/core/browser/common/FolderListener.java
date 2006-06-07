package org.openscada.da.core.browser.common;

import java.util.Collection;

import org.openscada.da.core.browser.Entry;


public interface FolderListener
{
    void changed ( Object tag, Collection<Entry> added, Collection<String> removed, boolean full );
}
