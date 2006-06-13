package org.openscada.utils.collection;

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.lang.Pair;

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
