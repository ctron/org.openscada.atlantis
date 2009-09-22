package org.openscada.hd.ui.data;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.utils.beans.AbstractPropertyChange;

public class HistoricalItemEntryBean extends AbstractPropertyChange
{
    private final String id;

    private final ConnectionEntryBean parent;

    public HistoricalItemEntryBean ( final ConnectionEntryBean parent, final String id )
    {
        this.parent = parent;
        this.id = id;
    }

    public HistoricalItemEntryBean ( final ConnectionEntryBean parent, final HistoricalItemInformation entry )
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
