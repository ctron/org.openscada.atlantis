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
        this._values = new LinkedList<Value> ();
    }

    public void add ( final Value value )
    {
        this._values.add ( value );
    }

    public void remove ( final Value value )
    {
        this._values.remove ( value );
    }

    public int size ()
    {
        return this._values.size ();
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._values == null ? 0 : this._values.hashCode () );
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
        final ListValue other = (ListValue)obj;
        if ( this._values == null )
        {
            if ( other._values != null )
            {
                return false;
            }
        }
        else if ( !this._values.equals ( other._values ) )
        {
            return false;
        }
        return true;
    }

    public List<Value> getValues ()
    {
        return this._values;
    }
}
