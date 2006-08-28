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

package org.openscada.da.core.server.browser;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.openscada.utils.str.StringHelper;

public class Location
{
    private String [] _location = new String[0];
    
    public Location ( String... location )
    {
        _location = location.clone ();
    }
    
    public Location ( Location arg0 )
    {
        _location = arg0._location.clone ();
    }
    
    public Location ()
    {
    }
    
    public Location ( List<String> location )
    {
        _location = location.toArray ( new String[location.size()] );
    }
    
    public String [] asArray ()
    {
        return _location;
    }
    
    public List<String> asList ()
    {
        return Arrays.asList ( _location );
    }
    
    @Override
    public String toString ()
    {
        return toString ( "/" );
    }
    
    public String toString ( String separator )
    {
        return separator + StringHelper.join ( _location, separator );
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode ( _location );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final Location other = (Location)obj;
        if ( !Arrays.equals ( _location, other._location ) )
            return false;
        return true;
    }
    
    public Stack<String> getPathStack ()
    {
        Stack<String> stack = new Stack<String> ();
        
        for ( int i = _location.length; i>0; i-- )
        {
            stack.push ( _location[i-1] );
        }
        
        return stack;
    }
}
