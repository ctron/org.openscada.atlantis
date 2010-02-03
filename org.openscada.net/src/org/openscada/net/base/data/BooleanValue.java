/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.net.base.data;

public class BooleanValue extends Value
{
    public final boolean value;

    public BooleanValue ( final boolean value )
    {
        super ();
        this.value = value;
    }

    public boolean getValue ()
    {
        return this.value;
    }

    @Override
    public String toString ()
    {
        return String.valueOf ( this.value );
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this.value ? 1231 : 1237 );
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
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final BooleanValue other = (BooleanValue)obj;
        if ( this.value != other.value )
        {
            return false;
        }
        return true;
    }

}
