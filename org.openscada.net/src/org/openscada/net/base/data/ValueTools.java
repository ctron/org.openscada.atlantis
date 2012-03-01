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
        return toLong ( value, defaultValue );
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
        return toInteger ( value, defaultValue );
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
