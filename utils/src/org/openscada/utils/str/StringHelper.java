package org.openscada.utils.str;

import java.util.Collection;
import java.util.Iterator;

public class StringHelper
{
    public static String join ( Collection items, String delimiter )
    {
        StringBuffer buffer = new StringBuffer ();
        Iterator iter = items.iterator ();
        
        while ( iter.hasNext () )
        {
            buffer.append ( iter.next() );
            if ( iter.hasNext () )
            {
                buffer.append ( delimiter );
            }
        }
        
        return buffer.toString();
    }
}
