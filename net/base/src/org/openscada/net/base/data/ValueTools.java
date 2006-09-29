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

package org.openscada.net.base.data;

import java.util.LinkedList;
import java.util.List;

public class ValueTools
{
    public static Long toLong ( Value value, Long defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
                return new Long ( ( (IntegerValue)value ).getValue () );
            else if ( value instanceof LongValue )
                return ( (LongValue)value ).getValue ();
            else if ( value instanceof DoubleValue )
                return (long) ( (DoubleValue)value ).getValue ();
            else if ( value instanceof StringValue )
            {
                String data = ( (StringValue)value ).getValue ();
                return Long.decode ( data );
            }
            else
                return defaultValue;
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }

    public static long toLong ( Value value, long defaultValue )
    {
        return toLong ( value, new Long ( defaultValue ) );
    }

    public static Integer toInteger ( Value value, Integer defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
                return ( (IntegerValue)value ).getValue ();
            else if ( value instanceof LongValue )
                return new Integer ( (int) ( (LongValue)value ).getValue () );
            else if ( value instanceof DoubleValue )
                return (int) ( (DoubleValue)value ).getValue ();
            else if ( value instanceof StringValue )
            {
                String data = ( (StringValue)value ).getValue ();
                return Integer.decode ( data );
            }
            else
                return defaultValue;
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }

    public static int toInteger ( Value value, int defaultValue )
    {
        return toInteger ( value, new Integer ( defaultValue ) );
    }

    public static ListValue toStringList ( Iterable<?> list )
    {
        ListValue listValue = new ListValue ();

        for ( Object obj : list )
        {
            listValue.add ( new StringValue ( obj.toString () ) );
        }

        return listValue;
    }

    public static List<String> fromStringList ( ListValue list )
    {
        List<String> newList = new LinkedList<String> ();

        for ( Value value : list.getValues () )
        {
            newList.add ( value.toString () );
        }

        return newList;
    }
}
