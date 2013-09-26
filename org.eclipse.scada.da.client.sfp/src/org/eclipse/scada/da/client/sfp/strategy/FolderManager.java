/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.da.client.sfp.strategy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.da.client.FolderListener;
import org.eclipse.scada.da.client.sfp.ConnectionHandler;
import org.eclipse.scada.da.core.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderManager
{

    private final static Logger logger = LoggerFactory.getLogger ( FolderManager.class );

    private final ConnectionHandler connectionHandler;

    private final Folder rootFolder;

    private final Map<Location, FolderListener> folderListeners = new HashMap<> ();

    public FolderManager ( final ConnectionHandler connectionHandler )
    {
        this.connectionHandler = connectionHandler;
        this.rootFolder = new Folder ( connectionHandler.getExecutor (), null, new Location () );
    }

    public void dispose ()
    {
        this.rootFolder.dispose ();
    }

    public void subscribeFolder ( final Location location )
    {
    }

    public void unsubscribeFolder ( final Location location )
    {
    }

    public void addEntry ( final Location location, final String name, final String itemId, final String description )
    {
        final Folder folder = this.rootFolder.findFolder ( location.getPathStack (), true );
        folder.addItemEntry ( name, itemId, description );
    }

    public void removeEntry ( final Location location, final String name )
    {
        final Folder folder = this.rootFolder.findFolder ( location.getPathStack (), false );
        if ( folder == null )
        {
            return;
        }

        folder.removeItemEntry ( name );
    }

    public void setFolderListener ( final Location location, final FolderListener listener )
    {
        logger.debug ( "Setting folder listener - location: {}, listener: {}", location, listener );

        this.folderListeners.put ( location, listener );
        final Folder folder = this.rootFolder.findFolder ( location.getPathStack (), false );
        if ( folder != null )
        {
            folder.setListener ( listener );
        }
    }

    protected void execute ( final Runnable command )
    {
        this.connectionHandler.getExecutor ().execute ( command );
    }

}
