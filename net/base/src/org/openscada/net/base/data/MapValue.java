package org.openscada.net.base.data;

import java.util.HashMap;
import java.util.Map;

public class MapValue extends Value
{
    private Map < String, Value > _values = null;
    
    public MapValue ()
    {
        _values = new HashMap < String, Value > ();
    }
    
    public Map < String, Value > getValues ()
    {
        return _values;
    }
    
    public void put ( String key, Value value )
    {
        _values.put ( key, value );
    }
    
    public void remove ( String key )
    {
        _values.remove ( key );
    }
    
    public Value get ( String key )
    {
        return _values.get ( key );
    }
    
    public boolean containsKey ( String key )
    {
        return _values.containsKey ( key );
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
        final MapValue other = (MapValue)obj;
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
}
