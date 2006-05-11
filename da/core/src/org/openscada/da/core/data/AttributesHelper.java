package org.openscada.da.core.data;

import java.util.Map;

public class AttributesHelper
{
    /**
     * merges the difference attributes into the target
     * @param target the attributes to merge the difference in
     * @param diff the difference attributes
     */
    public static void mergeAttributes ( Map<String,Variant> target, Map<String,Variant> diff )
    {
        for ( Map.Entry<String,Variant> entry : diff.entrySet() )
        {
            if ( entry.getKey() == null )
                continue;
            
            if ( entry.getValue() == null )
            {
                target.remove(entry.getKey());
            }
            else if ( entry.getValue().isNull() )
            {
                target.remove(entry.getKey());
            }
            else
            {
                target.put(new String(entry.getKey()), new Variant(entry.getValue()));
            }
        }
    }
}
