/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc2.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.opc2.connection.OPCController;
import org.openscada.da.server.opc2.connection.OPCItem;

public class OPCTreeFolder implements org.openscada.da.server.browser.common.Folder, BrowseRequestListener
{

    protected FolderCommon folderImpl = new FolderCommon ();

    protected boolean refreshed = false;

    private final OPCController controller;

    private Collection<String> path = new ArrayList<String> ();

    public OPCTreeFolder ( final OPCController controller, final Collection<String> path )
    {
        this.controller = controller;
        this.path = path;
    }

    public void added ()
    {
        this.folderImpl.added ();
    }

    public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this.folderImpl.list ( path );
    }

    public void removed ()
    {
        this.folderImpl.removed ();
    }

    public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        checkRefresh ();
        this.folderImpl.subscribe ( path, listener, tag );
    }

    protected void checkRefresh ()
    {
        synchronized ( this )
        {
            if ( this.refreshed )
            {
                return;
            }
            this.refreshed = true;
        }
        refresh ();
    }

    private void refresh ()
    {
        this.controller.getBrowserManager ().addBrowseRequest ( new BrowseRequest ( this.path ), this );
    }

    public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this.folderImpl.unsubscribe ( path, tag );
    }

    public void browseComplete ( final BrowseResult result )
    {
        final Map<String, Folder> folders = new HashMap<String, Folder> ();
        final Map<String, DataItem> items = new HashMap<String, DataItem> ();

        for ( final String branch : result.getBranches () )
        {
            final Collection<String> path = new ArrayList<String> ( this.path );
            path.add ( branch );
            folders.put ( branch, new OPCTreeFolder ( this.controller, path ) );
        }

        for ( final BrowseResultEntry entry : result.getLeaves () )
        {
            final OPCItem item = this.controller.getItemManager ().registerItem ( entry.getItemId (), entry.getIoDirections (), null );
            items.put ( entry.getEntryName (), item );
        }

        // bulkd add entries
        this.folderImpl.add ( folders, items );
    }

    public void browseError ( final Throwable error )
    {
    }
}
