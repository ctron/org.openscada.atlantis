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

package org.openscada.da.client.base.realtime;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.da.ui.connection.dnd.ItemTransfer;

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
        if ( data instanceof Item[] )
        {
            dropItems ( ( (Item[])data ) );
            return true;
        }
        if ( data instanceof String )
        {
            dropString ( data );
            return true;
        }
        return false;
    }

    private void dropString ( final Object data )
    {
        final TreeViewer viewer = (TreeViewer)getViewer ();
        final String toks[] = ( (String)data ).split ( "[\\n\\r]+" );
        for ( final String tok : toks )
        {
            try
            {
                final URI uri = new URI ( tok );
                if ( uri.getFragment () != null )
                {
                    final Item item = new Item ( uri.toString (), uri.getFragment () );
                    dropItem ( item, viewer );
                }

            }
            catch ( final URISyntaxException e )
            {
            }
        }
    }

    private void dropItems ( final Item[] items )
    {
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
    }

    private void dropItem ( final Item item, final TreeViewer viewer ) throws URISyntaxException
    {
        final ListEntry entry = new ListEntry ();
        entry.setDataItem ( new Item ( item ) );
        this.list.add ( entry );
    }

    @Override
    public boolean validateDrop ( final Object target, final int operation, final TransferData transferData )
    {
        return ItemTransfer.getInstance ().isSupportedType ( transferData ) || TextTransfer.getInstance ().isSupportedType ( transferData );
    }

}
