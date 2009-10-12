package org.openscada.da.ui.connection.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.da.client.FolderListener;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;

public abstract class FolderObserver extends WritableSet implements FolderListener
{

    private final Map<String, FolderEntryWrapper> entries = new HashMap<String, FolderEntryWrapper> ();

    private FolderEntryWrapper parent;

    public FolderObserver ()
    {
    }

    protected void setFolderManager ( final FolderEntryWrapper parent )
    {

        if ( this.parent != null )
        {
            this.parent.getFolderManager ().removeFolderListener ( this, parent.getLocation () );
        }

        this.parent = parent;

        if ( parent == null )
        {
            clear ();
        }
        else
        {
            parent.getFolderManager ().addFolderListener ( this, parent.getLocation () );
        }
    }

    @Override
    public synchronized void dispose ()
    {
        if ( this.parent != null )
        {
            this.parent.getFolderManager ().removeFolderListener ( this, this.parent.getLocation () );
        }
        super.dispose ();
    }

    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                handleChange ( added, removed, full );
            }
        } );
    }

    private void handleChange ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        setStale ( true );

        try
        {
            if ( removed != null )
            {
                for ( final String name : removed )
                {
                    final FolderEntryWrapper entry = this.entries.remove ( name );
                    if ( entry != null )
                    {
                        remove ( entry );
                    }
                }
            }
            if ( added != null )
            {
                for ( final Entry entry : added )
                {
                    final FolderEntryWrapper newEntry;
                    newEntry = new FolderEntryWrapper ( this.parent, entry, new Location ( this.parent.getLocation (), entry.getName () ) );

                    final FolderEntryWrapper oldEntry = this.entries.put ( entry.getName (), newEntry );
                    if ( oldEntry != null )
                    {
                        remove ( oldEntry );
                    }
                    add ( newEntry );
                }
            }
        }
        finally
        {
            setStale ( false );
        }
    }
}