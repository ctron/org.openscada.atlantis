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
import org.openscada.da.server.common.MemoryDataItem;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.da.server.test.Hive;
import org.openscada.utils.collection.MapBuilder;

public class MemoryCellItem extends DataItemOutput
{
    private Hive _hive;
    
    private Map<Integer,MemoryDataItem> _items = new HashMap<Integer,MemoryDataItem> ();
    private AttributeManager _attributes = null;
    private FolderCommon _folder = null;
    
    public MemoryCellItem ( Hive hive, String name, FolderCommon folder )
    {
        super ( name );
        _hive = hive;
        _folder = folder;
        
        _attributes = new AttributeManager ( this );
        
        updateCells ( 0 );
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes.getCopy();
    }

    public WriteAttributeResults setAttributes ( Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

    public void setValue ( Variant value ) throws InvalidOperationException,
            NullValueException, NotConvertableException
    {
        int num = value.asInteger ();
        
        updateCells ( num );
    }
    
    private void setSizeAttribute ( int num )
    {
        _attributes.update ( "size", new Variant(num) );
    }

    private void updateCells ( int num )
    {
        if ( num < 0 )
            num = 0;
        
        synchronized ( _items )
        {
            Map<Integer,MemoryDataItem> newItems = new HashMap<Integer,MemoryDataItem> ( num );
            
            int pos;
            for ( pos = 0; pos < num && pos < _items.size (); pos++ )
            {
                if ( _items.containsKey ( pos ) )
                {
                    newItems.put ( pos, _items.get ( pos ) );
                    _items.remove ( pos );
                }
            }
            
            for ( Map.Entry<Integer, MemoryDataItem> entry : _items.entrySet () )
            {
                _folder.remove ( entry.getKey ().toString () );
                _hive.unregisterItem ( entry.getValue () );
            }
            
            for ( int i = pos ; i < num ; i++ )
            {
                MemoryDataItem item = new MemoryDataItem ( getInformation ().getName () + "-" + i );
                _hive.registerItem ( item );
                _folder.add ( String.valueOf ( i ), item, new MapBuilder<String, Variant>()
                        .put ( "description", new Variant ( "Cell #" + i + " of " + num + " automaticall provided memory cells." ) )
                        .getMap ()
                    );
                newItems.put ( i, item );
            }
            
            _items = newItems;
            
            setSizeAttribute ( num );
        }
    }
}
