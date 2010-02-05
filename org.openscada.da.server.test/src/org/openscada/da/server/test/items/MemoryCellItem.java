/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeManager;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.test.Hive;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class MemoryCellItem extends DataItemOutput
{
    private final Hive hive;

    private Map<Integer, MemoryItemChained> items = new HashMap<Integer, MemoryItemChained> ();

    private AttributeManager attributes = null;

    private FolderCommon folder = null;

    public MemoryCellItem ( final Hive hive, final String name, final FolderCommon folder )
    {
        super ( name );
        this.hive = hive;
        this.folder = folder;

        this.attributes = new AttributeManager ( this );

        updateCells ( 0 );
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes.getCopy ();
    }

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        int num;
        try
        {
            num = value.asInteger ();
            updateCells ( num );
            return new InstantFuture<WriteResult> ( new WriteResult () );
        }
        catch ( final Throwable e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    private void setSizeAttribute ( final int num )
    {
        this.attributes.update ( "size", new Variant ( num ) );
    }

    private void updateCells ( int num )
    {
        if ( num < 0 )
        {
            num = 0;
        }

        synchronized ( this.items )
        {
            final Map<Integer, MemoryItemChained> newItems = new HashMap<Integer, MemoryItemChained> ( num );

            int pos;
            for ( pos = 0; pos < num && pos < this.items.size (); pos++ )
            {
                if ( this.items.containsKey ( pos ) )
                {
                    newItems.put ( pos, this.items.get ( pos ) );
                    this.items.remove ( pos );
                }
            }

            for ( final Map.Entry<Integer, MemoryItemChained> entry : this.items.entrySet () )
            {
                this.folder.remove ( entry.getKey ().toString () );
                this.hive.unregisterItem ( entry.getValue () );
            }

            for ( int i = pos; i < num; i++ )
            {
                final MemoryItemChained item = new MemoryItemChained ( getInformation ().getName () + "-" + i );

                MemoryChainedItem.applyDefaultInputChain ( this.hive, item );

                this.hive.registerItem ( item );
                this.folder.add ( String.valueOf ( i ), item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Cell #" + i + " of " + num + " automaticall provided memory cells." ) ).getMap () );
                newItems.put ( i, item );
            }

            this.items = newItems;

            setSizeAttribute ( num );
        }
    }
}
