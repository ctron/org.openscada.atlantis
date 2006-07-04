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

public class ListValue extends Value
{

    private List<Value> _values = null;

    public ListValue ()
    {
        _values = new LinkedList < Value > ();
    }
    
    public void add ( Value value )
    {
        _values.add ( value );
    }
    
    public void remove ( Value value )
    {
        _values.remove ( value );
    }
    
    public int size ()
    {
        return _values.size ();
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _values == null ) ? 0 : _values.hashCode () );
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
        final ListValue other = (ListValue)obj;
        if ( _values == null )
        {
            if ( other._values != null )
                return false;
        }
        else
            if ( !_values.equals ( other._values ) )
                return false;
        return true;
    }
    
    public List<Value> getValues ()
    {
        return _values;
    }
}
