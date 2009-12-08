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
            final Boolean b = o2.asBoolean ( null );
            if ( b == null )
            {
                return -1;
            }
            return Boolean.valueOf ( o1.asBoolean () ).compareTo ( b );
        }
        else if ( o2.isBoolean () )
        {
            final Boolean b = o1.asBoolean ( null );
            if ( b == null )
            {
                return 1;
            }
            return Boolean.valueOf ( o2.asBoolean () ).compareTo ( o1.asBoolean ( b ) );
        }
        try
        {
            if ( o1.isString () && o2.isNumber () )
            {
                return o2.compareTo ( o1 );
            }
            else if ( o1.isString () && o2.isString () )
            {
                return o2.asString ().compareTo ( o1.asString () );
            }
            return Double.valueOf ( o1.asDouble () ).compareTo ( o2.asDouble () );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException ( e );
        }
    }
}
