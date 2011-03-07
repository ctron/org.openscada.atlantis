/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.preload;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.configuration.ItemDescription;
import org.openscada.da.server.opc.connection.OPCItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractItemSource implements ItemSource
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractItemSource.class );

    protected FolderItemFactory factory;

    protected FolderItemFactory itemsFactory;

    private final String id;

    private OPCItemManager itemManager;

    public AbstractItemSource ( final String id )
    {
        this.id = id;
    }

    @Override
    public String getId ()
    {
        return this.id;
    }

    @Override
    public void activate ( final FolderItemFactory factory, final OPCItemManager itemManager )
    {
        this.factory = factory;
        this.itemsFactory = factory.createSubFolderFactory ( "items" );

        this.itemManager = itemManager;
    }

    @Override
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
