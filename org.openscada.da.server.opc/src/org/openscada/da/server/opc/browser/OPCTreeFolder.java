/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.opc.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.opc.connection.OPCController;
import org.openscada.utils.str.StringHelper;

public class OPCTreeFolder implements org.openscada.da.server.browser.common.Folder, BrowseRequestListener
{
    private static Logger logger = Logger.getLogger ( OPCTreeFolder.class );

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
            if ( logger.isInfoEnabled () )
            {
                logger.info ( String.format ( "Need to refresh folder (%s)", getPath () ) );
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
        try
        {
            this.folderImpl.unsubscribe ( path, tag );
        }
        finally
        {
            checkCleanUp ();
        }
    }

    protected void checkCleanUp ()
    {
        synchronized ( this )
        {
            if ( !this.folderImpl.hasSubscribers () && this.refreshed )
            {
                logger.info ( String.format ( "No more subscribers. Clearing folder. (%s)", getPath () ) );
                this.refreshed = false;
                this.folderImpl.clear ();
            }
        }
    }

    public void browseComplete ( final BrowseResult result )
    {
        final Map<String, Folder> folders = new HashMap<String, Folder> ();
        final Map<String, DataItemInformation> items = new HashMap<String, DataItemInformation> ();

        for ( final String branch : result.getBranches () )
        {
            final Collection<String> path = new ArrayList<String> ( this.path );
            path.add ( branch );
            folders.put ( branch, new OPCTreeFolder ( this.controller, path ) );
        }

        for ( final BrowseResultEntry entry : result.getLeaves () )
        {
            // create the openscada item id from the opc item id
            final String itemId = this.controller.getItemManager ().createItemId ( entry.getItemId () );
            final DataItemInformation itemInformation = new DataItemInformationBase ( itemId, entry.getIoDirections () );

            items.put ( entry.getEntryName (), itemInformation );
        }

        // bulk add entries
        this.folderImpl.add ( folders, items );
    }

    public void browseError ( final Throwable error )
    {
        logger.info ( "Failed to browse", error );
    }

    protected String getPath ()
    {
        return StringHelper.join ( this.path, "/" );
    }
}
