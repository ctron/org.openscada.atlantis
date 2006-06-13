package org.openscada.da.core.browser;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.openscada.utils.str.StringHelper;

public class Location
{
    private String [] _location = new String[0];
    
    public Location ( String... location )
    {
        _location = location.clone ();
    }
    
    public Location ( Location arg0 )
    {
        _location = arg0._location.clone ();
    }
    
    public Location ()
    {
    }
    
    public Location ( List<String> location )
    {
        _location = location.toArray ( new String[location.size()] );
    }
    
    public String [] asArray ()
    {
        return _location;
    }
    
    public List<String> asList ()
    {
        return Arrays.asList ( _location );
    }
    
    @Override
    public String toString ()
    {
        return toString ( "/" );
    }
    
    public String toString ( String separator )
    {
        return separator + StringHelper.join ( _location, separator );
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode ( _location );
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
        final Location other = (Location)obj;
        if ( !Arrays.equals ( _location, other._location ) )
            return false;
        return true;
    }
    
    public Stack<String> getPathStack ()
    {
        Stack<String> stack = new Stack<String> ();
        
        for ( int i = _location.length; i>0; i-- )
        {
            stack.push ( _location[i-1] );
        }
        
        return stack;
    }
}
