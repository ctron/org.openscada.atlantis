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

package org.openscada.da.base.realtime;

import java.net.URISyntaxException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.base.connection.ConnectionManager;
import org.openscada.da.base.dnd.ItemTransfer;
import org.openscada.da.base.item.Item;
import org.openscada.da.client.ItemManager;

public class ItemDropAdapter extends ViewerDropAdapter
{

    private final RealtimeListAdapter list;

    public ItemDropAdapter ( final Viewer viewer, final RealtimeListAdapter list )
    {
        super ( viewer );
        this.list = list;
        setFeedbackEnabled ( true );
        setSelectionFeedbackEnabled ( true );
    }

    @Override
    public boolean performDrop ( final Object data )
    {
        final Item[] items = (Item[])data;

        final TreeViewer viewer = (TreeViewer)getViewer ();

        for ( final Item item : items )
        {
            try
            {
                dropItem ( item, viewer );
            }
            catch ( final URISyntaxException e )
            {
                e.printStackTrace ();
            }
        }

        return true;
    }

    private void dropItem ( final Item item, final TreeViewer viewer ) throws URISyntaxException
    {
        final ConnectionInformation connectionInformation = ConnectionInformation.fromURI ( item.getConnectionString () );
        final ItemManager itemManager = ConnectionManager.getDefault ().getItemManager ( connectionInformation, true );

        if ( itemManager != null )
        {
            final ListEntry entry = new ListEntry ();
            entry.setDataItem ( new Item ( item ), itemManager );
            this.list.add ( entry );
        }
    }

    @Override
    public boolean validateDrop ( final Object target, final int operation, final TransferData transferType )
    {
        return ItemTransfer.getInstance ().isSupportedType ( transferType );
    }

}
