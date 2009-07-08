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

package org.openscada.da.server.opc.preload;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.configuration.ItemDescription;
import org.openscada.da.server.opc.connection.OPCItemManager;

public abstract class AbstractItemSource implements ItemSource
{
    private static Logger logger = Logger.getLogger ( AbstractItemSource.class );

    protected FolderItemFactory factory;

    protected FolderItemFactory itemsFactory;

    private final String id;

    private OPCItemManager itemManager;

    public AbstractItemSource ( final String id )
    {
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }

    public void activate ( final FolderItemFactory factory, final OPCItemManager itemManager )
    {
        this.factory = factory;
        this.itemsFactory = factory.createSubFolderFactory ( "items" );

        this.itemManager = itemManager;
    }

    public void deactivate ()
    {
        this.itemManager = null;

        this.itemsFactory.dispose ();
        this.itemsFactory = null;
        this.factory.dispose ();
        this.factory = null;
    }

    protected void fireAvailableItemsChanged ( final Set<ItemDescription> items )
    {
        logger.info ( "Available items changed" );

        // first clear out the old items
        this.itemsFactory.disposeAllItems ();

        // bulk add the new items 
        final FolderCommon folder = this.itemsFactory.getFolder ();

        final Map<String, DataItemInformation> addItems = new HashMap<String, DataItemInformation> ();
        for ( final ItemDescription item : items )
        {
            addItems.put ( item.getId (), new DataItemInformationBase ( this.itemManager.createItemId ( item.getId () ), EnumSet.allOf ( IODirection.class ) ) );
        }

        // perform the add
        folder.add ( null, addItems );
    }
}
