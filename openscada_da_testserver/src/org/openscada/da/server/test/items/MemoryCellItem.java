/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeManager;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.test.Hive;
import org.openscada.utils.collection.MapBuilder;

public class MemoryCellItem extends DataItemOutput
{
    private final Hive _hive;

    private Map<Integer, MemoryItemChained> _items = new HashMap<Integer, MemoryItemChained> ();

    private AttributeManager _attributes = null;

    private FolderCommon _folder = null;

    public MemoryCellItem ( final Hive hive, final String name, final FolderCommon folder )
    {
        super ( name );
        this._hive = hive;
        this._folder = folder;

        this._attributes = new AttributeManager ( this );

        updateCells ( 0 );
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes.getCopy ();
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        final int num = value.asInteger ();

        updateCells ( num );
    }

    private void setSizeAttribute ( final int num )
    {
        this._attributes.update ( "size", new Variant ( num ) );
    }

    private void updateCells ( int num )
    {
        if ( num < 0 )
        {
            num = 0;
        }

        synchronized ( this._items )
        {
            final Map<Integer, MemoryItemChained> newItems = new HashMap<Integer, MemoryItemChained> ( num );

            int pos;
            for ( pos = 0; pos < num && pos < this._items.size (); pos++ )
            {
                if ( this._items.containsKey ( pos ) )
                {
                    newItems.put ( pos, this._items.get ( pos ) );
                    this._items.remove ( pos );
                }
            }

            for ( final Map.Entry<Integer, MemoryItemChained> entry : this._items.entrySet () )
            {
                this._folder.remove ( entry.getKey ().toString () );
                this._hive.unregisterItem ( entry.getValue () );
            }

            for ( int i = pos; i < num; i++ )
            {
                final MemoryItemChained item = new MemoryItemChained ( getInformation ().getName () + "-" + i );

                MemoryChainedItem.applyDefaultInputChain ( this._hive, item );

                this._hive.registerItem ( item );
                this._folder.add ( String.valueOf ( i ), item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Cell #" + i + " of " + num + " automaticall provided memory cells." ) ).getMap () );
                newItems.put ( i, item );
            }

            this._items = newItems;

            setSizeAttribute ( num );
        }
    }
}
