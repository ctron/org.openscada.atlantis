package org.openscada.net.base.data;

public class VoidValue extends Value
{
    @Override
    public int hashCode ()
    {
        return 31;
    }
    
    @Override
    public boolean equals ( Object other )
    {
        if ( this == other )
            return true;
        if ( other == null )
            return false;
        if ( getClass () != other.getClass () )
            return false;
        return true;
    }
    
    
}
