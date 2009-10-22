package org.openscada.ae.ui.data;

import org.openscada.ae.BrowserEntry;

public class BrowserEntryBean
{
    private final String id;

    private final ConnectionEntryBean parent;

    public BrowserEntryBean ( final ConnectionEntryBean parent, final String id )
    {
        this.parent = parent;
        this.id = id;
    }

    public BrowserEntryBean ( final ConnectionEntryBean parent, final BrowserEntry entry )
    {
        this.parent = parent;
        this.id = entry.getId ();
    }

    public ConnectionEntryBean getParent ()
    {
        return this.parent;
    }

    public String getId ()
    {
        return this.id;
    }

}
