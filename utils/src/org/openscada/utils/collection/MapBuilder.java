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

package org.openscada.utils.collection;

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.lang.Pair;

/**
 * A map builder which can created 
 * @author jens
 *
 * @param <Key>
 * @param <Value>
 */
public class MapBuilder<Key, Value>
{
    private Map<Key, Value> _map = null;

    public MapBuilder ( Map<Key, Value> map )
    {
        _map = map;
    }

    public MapBuilder ()
    {
        _map = new HashMap<Key, Value> ();
    }

    public MapBuilder<Key, Value> put ( Key key, Value value )
    {
        _map.put ( key, value );
        return this;
    }

    public MapBuilder<Key, Value> clear ()
    {
        _map.clear ();
        return this;
    }

    public Map<Key, Value> getMap ()
    {
        return _map;
    }

    public static <Key, Value> Map<Key, Value> toMap ( Pair<Key, Value> pair )
    {
        return toMap ( null, pair );
    }

    public static <Key, Value> Map<Key, Value> toMap ( Map<Key, Value> map, Pair<Key, Value> pair )
    {
        if ( map == null )
            map = new HashMap<Key, Value> ();

        map.put ( pair.first, pair.second );

        return map;
    }
}
