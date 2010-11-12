/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;

public class EventEntry
{
    /**
     * 
     */
    private DataItem item = null;

    private Variant value = null;

    private Map<String, Variant> attributes = null;

    public EventEntry ( final DataItem item, final Variant value, final Map<String, Variant> attributes )
    {
        this.item = item;
        this.value = value;

        if ( attributes == null )
        {
            this.attributes = new HashMap<String, Variant> ();
        }
        else
        {
            this.attributes = attributes;
        }
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this.attributes == null ? 0 : this.attributes.hashCode () );
        result = PRIME * result + ( this.item == null ? 0 : this.item.hashCode () );
        result = PRIME * result + ( this.value == null ? 0 : this.value.hashCode () );
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
        if ( this.attributes == null )
        {
            if ( other.attributes != null )
            {
                return false;
            }
        }
        else if ( !this.attributes.equals ( other.attributes ) )
        {
            return false;
        }
        if ( this.item == null )
        {
            if ( other.item != null )
            {
                return false;
            }
        }
        else if ( !this.item.equals ( other.item ) )
        {
            return false;
        }
        if ( this.value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !this.value.equals ( other.value ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( String.format ( "Value: %s, ", this.value ) );

        if ( this.attributes != null )
        {
            for ( final Map.Entry<String, Variant> entry : this.attributes.entrySet () )
            {
                sb.append ( String.format ( "'%s'=>'%s', ", entry.getKey (), entry.getValue () ) );
            }
        }

        return sb.toString ();
    }
}