package org.openscada.hd.ui.connection.views;

import org.openscada.hd.ui.connection.internal.FolderEntryWrapper;

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
