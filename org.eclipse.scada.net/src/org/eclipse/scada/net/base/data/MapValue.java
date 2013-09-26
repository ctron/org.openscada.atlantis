/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.net.base.data;

import java.util.HashMap;
import java.util.Map;

public class MapValue extends Value
{
    private final Map<String, Value> values;

    public MapValue ()
    {
        this.values = new HashMap<String, Value> ();
    }

    public MapValue ( final int initialCapacity )
    {
        this.values = new HashMap<String, Value> ( initialCapacity );
    }

    public MapValue ( final Map<String, Value> values )
    {
        this.values = values;
    }

    public Map<String, Value> getValues ()
    {
        return this.values;
    }

    public void put ( final String key, final Value value )
    {
        this.values.put ( key, value );
    }

    public void remove ( final String key )
    {
        this.values.remove ( key );
    }

    public Value get ( final String key )
    {
        return this.values.get ( key );
    }

    public boolean containsKey ( final String key )
    {
        return this.values.containsKey ( key );
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
        final MapValue other = (MapValue)obj;
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
}
