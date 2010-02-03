/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

import java.util.LinkedList;
import java.util.List;

public class ValueTools
{
    public static Long toLong ( final Value value, final Long defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
            {
                return Long.valueOf ( ( (IntegerValue)value ).getValue () );
            }
            else if ( value instanceof LongValue )
            {
                return ( (LongValue)value ).getValue ();
            }
            else if ( value instanceof DoubleValue )
            {
                return (long) ( (DoubleValue)value ).getValue ();
            }
            else if ( value instanceof StringValue )
            {
                final String data = ( (StringValue)value ).getValue ();
                return Long.decode ( data );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( final Exception e )
        {
            return defaultValue;
        }
    }

    public static long toLong ( final Value value, final long defaultValue )
    {
        return toLong ( value, Long.valueOf ( defaultValue ) );
    }

    public static Integer toInteger ( final Value value, final Integer defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
            {
                return ( (IntegerValue)value ).getValue ();
            }
            else if ( value instanceof LongValue )
            {
                return (int) ( (LongValue)value ).getValue ();
            }
            else if ( value instanceof DoubleValue )
            {
                return (int) ( (DoubleValue)value ).getValue ();
            }
            else if ( value instanceof StringValue )
            {
                final String data = ( (StringValue)value ).getValue ();
                return Integer.decode ( data );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( final Exception e )
        {
            return defaultValue;
        }
    }

    public static int toInteger ( final Value value, final int defaultValue )
    {
        return toInteger ( value, Integer.valueOf ( defaultValue ) );
    }

    public static ListValue toStringList ( final Iterable<?> list )
    {
        final ListValue listValue = new ListValue ();

        for ( final Object obj : list )
        {
            listValue.add ( new StringValue ( obj.toString () ) );
        }

        return listValue;
    }

    public static List<String> fromStringList ( final ListValue list )
    {
        final List<String> newList = new LinkedList<String> ();

        for ( final Value value : list.getValues () )
        {
            newList.add ( value.toString () );
        }

        return newList;
    }
}
