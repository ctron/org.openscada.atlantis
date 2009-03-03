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

package org.openscada.da.server.common.chain.test;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;

public class EventEntry
{
    /**
     * 
     */
    private DataItem _item = null;

    private Variant _value = null;

    private Map<String, Variant> _attributes = null;

    public EventEntry ( final DataItem item, final Variant value, final Map<String, Variant> attributes )
    {
        this._item = item;
        this._value = value;
        this._attributes = attributes;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._attributes == null ? 0 : this._attributes.hashCode () );
        result = PRIME * result + ( this._item == null ? 0 : this._item.hashCode () );
        result = PRIME * result + ( this._value == null ? 0 : this._value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        final EventEntry other = (EventEntry)obj;
        if ( this._attributes == null )
        {
            if ( other._attributes != null )
            {
                return false;
            }
        }
        else if ( !this._attributes.equals ( other._attributes ) )
        {
            return false;
        }
        if ( this._item == null )
        {
            if ( other._item != null )
            {
                return false;
            }
        }
        else if ( !this._item.equals ( other._item ) )
        {
            return false;
        }
        if ( this._value == null )
        {
            if ( other._value != null )
            {
                return false;
            }
        }
        else if ( !this._value.equals ( other._value ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( String.format ( "Value: %s, ", this._value ) );

        if ( this._attributes != null )
        {
            for ( final Map.Entry<String, Variant> entry : this._attributes.entrySet () )
            {
                sb.append ( String.format ( "'%s'=>'%s', ", entry.getKey (), entry.getValue () ) );
            }
        }

        return sb.toString ();
    }
}