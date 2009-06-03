/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.proxy.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.da.client.FolderManager;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItemInformationBase;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyFolder implements Folder, org.openscada.da.client.FolderListener
{
    private final static Logger logger = Logger.getLogger ( ProxyFolder.class );

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

    public void added ()
    {
        this.folder.added ();
    }

    public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this.folder.list ( path );
    }

    public void removed ()
    {
        checkDisconnect ( true );
        this.folder.removed ();
    }

    private void checkDisconnect ( final boolean force )
    {
        synchronized ( this )
        {
            if ( !this.initialized )
            {
                return;
            }
            if ( !force && this.folder.hasSubscribers () )
            {
                return;
            }

            this.initialized = false;
            logger.info ( String.format ( "Disconnect folder for location: %s", this.location ) );
        }
        this.folderManager.removeFolderListener ( this, this.location );
        this.folder.clear ();
    }

    public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        connect ();
        this.folder.subscribe ( path, listener, tag );
    }

    private void connect ()
    {
        synchronized ( this )
        {
            if ( this.initialized )
            {
                return;
            }
            this.initialized = true;
        }
        this.folderManager.addFolderListener ( this, this.location );
    }

    public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this.folder.unsubscribe ( path, tag );

        // check if we can disconnect
        checkDisconnect ( false );
    }

    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        try
        {
            handleFolderChanged ( added, removed, full );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to handle folder change", e );
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

        final Map<String, DataItemInformation> items = new HashMap<String, DataItemInformation> ();
        final Map<String, Folder> folders = new HashMap<String, Folder> ();

        // add items
        for ( final Entry entry : added )
        {
            if ( entry instanceof DataItemEntry )
            {
                final DataItemEntry dataItemEntry = (DataItemEntry)entry;
                final String itemId = this.proxyGroup.convertToProxyId ( dataItemEntry.getId () );
                if ( itemId != null )
                {
                    final DataItemInformation itemInformation = new DataItemInformationBase ( itemId, dataItemEntry.getIODirections () );
                    items.put ( entry.getName (), itemInformation );
                }
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
