package org.openscada.utils.collection;

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.lang.Pair;

public class MapBuilder
{
    /**
     * Build a map using an array of pairs
     * @param <Key> The key type
     * @param <Value> The value type
     * @param pairs A list of pairs
     * @return the new map (actually a HashMap)
     */
    public static <Key, Value> Map<Key, Value> toMap ( Iterable<Pair<Key, Value>> pairs)
    {
        Map<Key, Value> map = new HashMap<Key, Value> ();
        
        for ( Pair<Key, Value> pair : pairs )
        {
            map.put ( pair.first, pair.second );
        }
        
        return map;
    }
}
