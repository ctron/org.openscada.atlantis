package org.openscada.da.server.opc2.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.opc2.connection.OPCController;
import org.openscada.da.server.opc2.connection.OPCItem;

public class OPCTreeFolder implements org.openscada.da.server.browser.common.Folder, BrowseRequestListener
{

    protected FolderCommon folderImpl = new FolderCommon ();

    protected boolean refreshed = false;

    private OPCController controller;

    private Collection<String> path = new ArrayList<String> ();

    public OPCTreeFolder ( OPCController controller, Collection<String> path )
    {
        this.controller = controller;
        this.path = path;
    }

    public void added ()
    {
        folderImpl.added ();
    }

    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        return folderImpl.list ( path );
    }

    public void removed ()
    {
        folderImpl.removed ();
    }

    public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        checkRefresh ();
        folderImpl.subscribe ( path, listener, tag );
    }

    protected void checkRefresh ()
    {
        synchronized ( this )
        {
            if ( refreshed )
            {
                return;
            }
            refreshed = true;
        }
        refresh ();
    }

    private void refresh ()
    {
        this.controller.getBrowserManager ().addBrowseRequest ( new BrowseRequest ( this.path ), this );
    }

    public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        this.folderImpl.unsubscribe ( path, tag );
    }

    public void browseComplete ( BrowseResult result )
    {
        for ( String branch : result.getBranches () )
        {
            Map<String, Variant> attributes = new HashMap<String, Variant> ();
            Collection<String> path = new ArrayList<String> ( this.path );
            path.add ( branch );
            this.folderImpl.add ( branch, new OPCTreeFolder ( this.controller, path ), attributes );
        }

        for ( BrowseResultEntry entry : result.getLeaves () )
        {
            OPCItem item = this.controller.getItemManager ().registerItem ( entry.getItemId (), entry.getIoDirections (), null );
            this.folderImpl.add ( entry.getEntryName (), item, new HashMap<String, Variant> () );
        }
    }

    public void browseError ( Throwable error )
    {
    }
}
