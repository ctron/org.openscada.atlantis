package org.openscada.core;

import java.io.Serializable;
import java.util.Comparator;

public class VariantComparator implements Comparator<Variant>, Serializable
{
    private static final long serialVersionUID = -1639436273307044212L;

    public int compare ( Variant o1, Variant o2 )
    {
        // special cases
        if ( o1 == o2 )
        {
            return 0;
        }
        if ( o1 == null && o2.isNull () )
        {
            return 0;
        }
        if ( o2 == null && o1.isNull () )
        {
            return 0;
        }
        if ( o1.isNull () && o2.isNull () )
        {
            return 0;
        }
        if ( o1.isBoolean () )
        {
            return Boolean.valueOf ( o1.asBoolean () ).compareTo ( o2.asBoolean () );
        }
        try
        {
            if ( o1.isInteger () )
            {
                return Integer.valueOf ( o1.asInteger () ).compareTo ( o2.asInteger () );
            }
            if ( o1.isLong () )
            {
                return Long.valueOf ( o1.asLong () ).compareTo ( o2.asLong () );
            }
            if ( o1.isDouble () )
            {
                return Double.valueOf ( o1.asDouble () ).compareTo ( o2.asDouble () );
            }
            if ( o1.isString () )
            {
                return o1.asString ().compareTo ( o2.asString () );
            }
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException ( e );
        }
        throw new IllegalArgumentException ( "one of the two arguments " + o1 + ", " + o2 + " is not valid" );
    }
}
