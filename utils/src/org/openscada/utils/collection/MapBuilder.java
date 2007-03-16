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
 * A map builder which can create a HashMap by chained calls.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 * @param <Key> The key type
 * @param <Value> The value type
 */
public class MapBuilder<Key, Value>
{
    private Map<Key, Value> _map = null;

    /**
     * Create a new map builder with the provided map as input
     * @param map the content that should be used as initial content. The provided map will not be modified.
     */
    public MapBuilder ( final Map<Key, Value> map )
    {
        _map = new HashMap<Key, Value> ( map );
    }

    public MapBuilder ()
    {
        _map = new HashMap<Key, Value> ();
    }

    /**
     * Put a pair into the map held by the map builder
     * @param key The key
     * @param value The value
     * @return the current instance of the map builder
     */
    public final MapBuilder<Key, Value> put ( final Key key, final Value value )
    {
        _map.put ( key, value );
        return this;
    }

    /**
     * Clean the map held by the map builder
     * @return the current instance of the map builder
     */
    public final MapBuilder<Key, Value> clear ()
    {
        _map.clear ();
        return this;
    }

    /**
     * Get the map of the map builder.
     * @return The map
     */
    public final Map<Key, Value> getMap ()
    {
        return _map;
    }

    /**
     * Return a new map the containing only the provided value pair
     * @param <Key> The key type
     * @param <Value> The value type
     * @param pair The pair to add
     * @return the new map containing the pair
     */
    public static <Key, Value> Map<Key, Value> toMap ( final Pair<Key, Value> pair )
    {
        return toMap ( null, pair );
    }

    /**
     * Return a new map containing the provided pair, or add the pair to an already existing
     * map.
     * @param <Key> The key type
     * @param <Value> The value type
     * @param map The map to which the pair should be added (may be <code>null</code>)
     * @param pair The pair to add (may <em>not</em> be <code>null</code>)
     * @return The (new) map.
     */
    public static <Key, Value> Map<Key, Value> toMap ( Map<Key, Value> map, final Pair<Key, Value> pair )
    {
        if ( map == null )
        {
            map = new HashMap<Key, Value> ();
        }

        map.put ( pair.first, pair.second );

        return map;
    }
}
