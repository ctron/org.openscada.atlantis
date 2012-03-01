/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.net.base.data;

public class LongValue extends Value
{
    public final long value;

    private static LongValue[] staticValues = new LongValue[100];

    static
    {
        for ( int i = 0; i < staticValues.length; i++ )
        {
            staticValues[i] = new LongValue ( i );
        }
    }

    public static LongValue valueOf ( final long value )
    {
        if ( value >= 0 && value < staticValues.length )
        {
            return staticValues[(int)value];
        }
        else
        {
            return new LongValue ( value );
        }
    }

    private LongValue ( final long value )
    {
        super ();
        this.value = value;
    }

    public long getValue ()
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
        result = PRIME * result + (int) ( this.value ^ this.value >>> 32 );
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
        final LongValue other = (LongValue)obj;
        if ( this.value != other.value )
        {
            return false;
        }
        return true;
    }
}
