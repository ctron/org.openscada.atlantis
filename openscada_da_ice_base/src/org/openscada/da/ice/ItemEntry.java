/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.ice;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;

public class ItemEntry implements DataItemEntry
{
    private EnumSet<IODirection> _ioDirections = EnumSet.noneOf ( IODirection.class );

    private String _id = "";

    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private String _name = "";

    public ItemEntry ( OpenSCADA.DA.Browser.ItemEntry entry )
    {
        super ();
        _id = entry.itemId;
        _name = entry.name;
        _attributes = AttributesHelper.fromIce ( entry.attributes );
        _ioDirections = EnumSet.noneOf ( IODirection.class );

        for ( int i = 0; i < entry.ioDirectionsM.length; i++ )
        {
            switch ( entry.ioDirectionsM[i].value () )
            {
            case OpenSCADA.DA.IODirection._INPUT:
                _ioDirections.add ( IODirection.INPUT );
                break;
            case OpenSCADA.DA.IODirection._OUTPUT:
                _ioDirections.add ( IODirection.OUTPUT );
                break;
            }
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public String getId ()
    {
        return _id;
    }

    public EnumSet<IODirection> getIODirections ()
    {
        return _ioDirections;
    }

    public String getName ()
    {
        return _name;
    }
}
