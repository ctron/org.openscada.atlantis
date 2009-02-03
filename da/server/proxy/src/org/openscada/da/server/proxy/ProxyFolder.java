package org.openscada.da.server.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openscada.da.client.FolderManager;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItem;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyFolder implements Folder, org.openscada.da.client.FolderListener
{
    private final FolderCommon folder = new FolderCommon ();

    private final Location location;

    private boolean initialized = false;

    private final FolderManager folderManager;

    private final ProxyGroup proxyGroup;

    /**
     * @param folderManager
     * @param proxyGroup
     * @param location
     */
    public ProxyFolder ( final FolderManager folderManager, final ProxyGroup proxyGroup, final Location location )
    {
        this.folderManager = folderManager;
        this.location = location;
        this.proxyGroup = proxyGroup;
    }

    @Override
    public void added ()
    {
        this.folder.added ();
    }

    @Override
    public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this.folder.list ( path );
    }

    @Override
    public void removed ()
    {
        disconnect ();
        this.folder.removed ();
    }

    private void disconnect ()
    {
        if ( this.initialized )
        {
            this.initialized = false;
            this.folderManager.removeFolderListener ( this, this.location );
            this.folder.clear ();
        }
    }

    @Override
    public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        connect ();
        this.folder.subscribe ( path, listener, tag );
    }

    private void connect ()
    {
        if ( !this.initialized )
        {
            this.initialized = true;
            this.folderManager.addFolderListener ( this, this.location );
        }
    }

    @Override
    public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this.folder.unsubscribe ( path, tag );
    }

    @Override
    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        try
        {
            handleFolderChanged ( added, removed, full );
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
    }

    private void handleFolderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        if ( full )
        {
            this.folder.clear ();
        }

        // remove items
        for ( final String entry : removed )
        {
            this.folder.remove ( entry );
        }

        final Map<String, DataItem> items = new HashMap<String, DataItem> ();
        final Map<String, Folder> folders = new HashMap<String, Folder> ();

        // add items
        for ( final Entry entry : added )
        {
            if ( entry instanceof DataItemEntry )
            {
                final DataItemEntry dataItemEntry = (DataItemEntry)entry;
                final DataItem proxyItem = this.proxyGroup.realizeItem ( this.proxyGroup.convertToProxyId ( dataItemEntry.getId () ) );
                items.put ( entry.getName (), proxyItem );
            }
            else if ( entry instanceof FolderEntry )
            {
                final List<String> location = new ArrayList<String> ( this.location.asList () );
                location.add ( entry.getName () );
                folders.put ( entry.getName (), new ProxyFolder ( this.folderManager, this.proxyGroup, new Location ( location ) ) );
            }
        }
        this.folder.add ( folders, items );
    }
}
