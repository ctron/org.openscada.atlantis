package org.openscada.core;

import java.io.Serializable;
import java.util.Comparator;

public class VariantComparator implements Comparator<Variant>, Serializable
{
    private static final long serialVersionUID = -1639436273307044212L;

    public int compare ( final Variant o1, final Variant o2 )
    {
        // special cases
        if ( o1 == o2 )
        {
            return 0;
        }
        if ( ( o1 == null ) && ( o2 == null ) )
        {
            return 0;
        }
        if ( ( o1 == null ) && o2.isNull () )
        {
            return 0;
        }
        if ( ( o2 == null ) && o1.isNull () )
        {
            return 0;
        }
        if ( ( o1 != null ) && ( o2 != null ) && o1.isNull () && o2.isNull () )
        {
            return 0;
        }
        // check if one of the parameters is null
        if ( ( o1 == null ) || o1.isNull () )
        {
            return -1;
        }
        if ( ( o2 == null ) || o2.isNull () )
        {
            return 1;
        }
        // boolean
        if ( o1.isBoolean () && o2.isBoolean () )
        {
            return o1.asBoolean ( false ).compareTo ( o2.asBoolean ( false ) );
        }
        if ( o1.isBoolean () )
        {
            return -1;
        }
        if ( o2.isBoolean () )
        {
            return 1;
        }
        // number
        if ( o1.isNumber () && o2.isNumber () )
        {
            return o1.asDouble ( 0.0 ).compareTo ( o2.asDouble ( 0.0 ) );
        }
        if ( o1.isNumber () )
        {
            return -1;
        }
        if ( o2.isNumber () )
        {
            return 1;
        }
        // string
        return o1.asString ( "" ).compareTo ( o2.asString ( "" ) );
    }
}
