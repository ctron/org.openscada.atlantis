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

package org.openscada.utils.lang;

public class Pair<T1, T2>
{
    public T1 first = null;

    public T2 second = null;

    public Pair ( T1 first, T2 second )
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( first == null ) ? 0 : first.hashCode () );
        result = prime * result + ( ( second == null ) ? 0 : second.hashCode () );
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        Pair other = (Pair)obj;
        if ( first == null )
        {
            if ( other.first != null )
                return false;
        }
        else if ( !first.equals ( other.first ) )
            return false;
        if ( second == null )
        {
            if ( other.second != null )
                return false;
        }
        else if ( !second.equals ( other.second ) )
            return false;
        return true;
    }
}
