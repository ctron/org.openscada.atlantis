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

package org.openscada.da.server.opc.preload;

import java.util.HashSet;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.configuration.ItemDescription;
import org.openscada.da.server.opc.connection.OPCItemManager;

public class InMemoryAbstractItemSource extends AbstractItemSource
{
    private final Set<ItemDescription> items;

    public InMemoryAbstractItemSource ( final String id, final Set<String> itemIds )
    {
        super ( id );

        this.items = new HashSet<ItemDescription> ();
        for ( final String itemId : itemIds )
        {
            this.items.add ( new ItemDescription ( itemId ) );
        }
    }

    @Override
    public void activate ( final FolderItemFactory factory, final OPCItemManager itemManager )
    {
        super.activate ( factory, itemManager );
        fireAvailableItemsChanged ( this.items );

        this.factory.createCommand ( "addItem" ).addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                InMemoryAbstractItemSource.this.addItem ( value.asString ( null ) );
            }
        } );
    }

    protected void addItem ( final String opcItemId )
    {
        if ( opcItemId == null )
        {
            return;
        }
        this.items.add ( new ItemDescription ( opcItemId ) );
        fireAvailableItemsChanged ( this.items );
    }
}
