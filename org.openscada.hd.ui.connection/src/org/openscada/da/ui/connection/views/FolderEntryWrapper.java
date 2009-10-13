package org.openscada.da.ui.connection.views;

import org.eclipse.core.runtime.IAdaptable;
import org.openscada.da.client.FolderManager;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;

public class FolderEntryWrapper implements IAdaptable
{
    private final FolderManager folderManager;

    private final FolderEntryWrapper parent;

    private final Entry entry;

    private final Location location;

    public FolderEntryWrapper ( final FolderManager folderManager )
    {
        this.folderManager = folderManager;
        this.parent = null;
        this.entry = null;
        this.location = new Location ();
    }

    public FolderEntryWrapper ( final FolderEntryWrapper parent, final Entry entry, final Location location )
    {
        this.folderManager = parent.getFolderManager ();
        this.parent = parent;
        this.entry = entry;
        this.location = location;
    }

    public Location getLocation ()
    {
        return this.location;
    }

    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }

    public Entry getEntry ()
    {
        return this.entry;
    }

    public FolderEntryWrapper getParent ()
    {
        return this.parent;
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        if ( adapter == FolderEntry.class && this.entry instanceof FolderEntry )
        {
            return this.entry;
        }
        if ( adapter == DataItemEntry.class && this.entry instanceof DataItemEntry )
        {
            return this.entry;
        }

        return null;
    }

}
