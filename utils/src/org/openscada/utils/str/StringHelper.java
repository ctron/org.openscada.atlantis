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
    
    public static String join ( Object [] items, String delimiter )
    {
        StringBuffer buffer = new StringBuffer ();
        
        for ( int i = 0; i < items.length; i++ )
        {
            if ( i != 0 )
                buffer.append ( delimiter );
            
            buffer.append ( items[i] );
        }
        
        return buffer.toString();
    }
}
