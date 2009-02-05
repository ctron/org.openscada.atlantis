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

package org.openscada.utils.str;

import java.util.Collection;
import java.util.Iterator;

/**
 * A helper class for string operations
 * @author Jens Reimann
 *
 */
public class StringHelper
{
    /**
     * Join a collection of objects to a string using a delimiter.
     * 
     * The method will take the {@link Object#toString()} result of each collection
     * member and add it to the result string. Delimiters are only placed between
     * elements
     * @param items The items to convert
     * @param delimiter the delimiter to use
     * @return the result string
     */
    public static String join ( Collection<?> items, String delimiter )
    {
        StringBuffer buffer = new StringBuffer ();
        Iterator<?> iter = items.iterator ();

        while ( iter.hasNext () )
        {
            buffer.append ( iter.next () );
            if ( iter.hasNext () )
            {
                buffer.append ( delimiter );
            }
        }

        return buffer.toString ();
    }

    /**
     * Join a collection of objects to a string using a delimiter.
     * 
     * The method will take the {@link Object#toString()} result of each collection
     * member and add it to the result string. Delimiters are only placed between
     * elements
     * @param items The items to convert
     * @param delimiter the delimiter to use
     * @return the result string
     */
    public static String join ( Object[] items, String delimiter )
    {
        StringBuffer buffer = new StringBuffer ();

        for ( int i = 0; i < items.length; i++ )
        {
            if ( i != 0 )
            {
                buffer.append ( delimiter );
            }

            buffer.append ( items[i] );
        }

        return buffer.toString ();
    }
}
