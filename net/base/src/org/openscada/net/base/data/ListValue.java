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
