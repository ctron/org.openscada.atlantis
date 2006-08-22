/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.core.common.chained.test;

import java.util.Map;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class EventEntry
{
    /**
     * 
     */
    private DataItem _item = null;
    private Variant _value = null;
    private Map<String, Variant> _attributes = null;
    
    public EventEntry ( DataItem item, Variant value, Map<String, Variant> attributes )
    {
        _item = item;
        _value = value;
        _attributes = attributes;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _attributes == null ) ? 0 : _attributes.hashCode () );
        result = PRIME * result + ( ( _item == null ) ? 0 : _item.hashCode () );
        result = PRIME * result + ( ( _value == null ) ? 0 : _value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        final EventEntry other = (EventEntry)obj;
        if ( _attributes == null )
        {
            if ( other._attributes != null )
                return false;
        }
        else
            if ( !_attributes.equals ( other._attributes ) )
                return false;
        if ( _item == null )
        {
            if ( other._item != null )
                return false;
        }
        else
            if ( !_item.equals ( other._item ) )
                return false;
        if ( _value == null )
        {
            if ( other._value != null )
                return false;
        }
        else
            if ( !_value.equals ( other._value ) )
                return false;
        return true;
    }
}