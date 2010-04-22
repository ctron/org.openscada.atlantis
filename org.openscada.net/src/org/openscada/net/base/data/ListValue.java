/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ListValue extends Value
{
    private final List<Value> values;

    public ListValue ()
    {
        this.values = new LinkedList<Value> ();
    }

    public ListValue ( final Value[] values )
    {
        this.values = new ArrayList<Value> ( Arrays.asList ( values ) );
    }

    public void add ( final Value value )
    {
        this.values.add ( value );
    }

    public void remove ( final Value value )
    {
        this.values.remove ( value );
    }

    public int size ()
    {
        return this.values.size ();
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this.values == null ? 0 : this.values.hashCode () );
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
        if ( this.values == null )
        {
            if ( other.values != null )
            {
                return false;
            }
        }
        else if ( !this.values.equals ( other.values ) )
        {
            return false;
        }
        return true;
    }

    public List<Value> getValues ()
    {
        return this.values;
    }
}
