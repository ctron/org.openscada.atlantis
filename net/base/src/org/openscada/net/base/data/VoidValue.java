package org.openscada.net.base.data;

/**
 * A void value type.
 * <p>
 * A void instance is equal to all other void instances since they don't have
 * a real value.
 * @author jens
 *
 */
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
