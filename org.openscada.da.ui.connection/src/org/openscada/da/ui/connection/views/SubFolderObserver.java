package org.openscada.da.ui.connection.views;

import org.openscada.da.ui.connection.internal.FolderEntryWrapper;

public class SubFolderObserver extends FolderObserver
{

    public SubFolderObserver ( final FolderEntryWrapper target )
    {
        super ();

        synchronized ( this )
        {
            setFolderManager ( target );
        }
    }

}
