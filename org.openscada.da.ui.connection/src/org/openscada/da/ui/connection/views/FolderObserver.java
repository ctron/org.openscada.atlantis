package org.openscada.da.ui.connection.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.FolderListener;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.ui.connection.internal.FolderEntryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FolderObserver extends WritableSet implements FolderListener
{

    private final static Logger logger = LoggerFactory.getLogger ( FolderObserver.class );

    private final Map<String, FolderEntryWrapper> entries = new HashMap<String, FolderEntryWrapper> ();

    private FolderEntryWrapper parent;

    public FolderObserver ()
    {
        super ( SWTObservables.getRealm ( Display.getDefault () ) );
    }

    protected void setFolderManager ( final FolderEntryWrapper parent )
    {
        if ( this.parent != null )
        {
            this.parent.getFolderManager ().removeFolderListener ( this, this.parent.getLocation () );
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
        logger.debug ( "Disposed" );

        if ( this.parent != null )
        {
            this.parent.getFolderManager ().removeFolderListener ( this, this.parent.getLocation () );
        }
        super.dispose ();
    }

    public synchronized void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        if ( isDisposed () )
        {
            logger.debug ( "Folder already disposed" );
            return;
        }

        getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                handleChange ( added, removed, full );
            }
        } );
    }

    private synchronized void handleChange ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        if ( isDisposed () )
        {
            logger.debug ( "Folder already disposed" );
            return;
        }

        setStale ( true );

        try
        {
            if ( full )
            {
                clear ();
            }

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