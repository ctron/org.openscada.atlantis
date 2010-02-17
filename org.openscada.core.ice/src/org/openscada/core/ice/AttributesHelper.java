package org.openscada.core.ice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscada.core.Variant;

import OpenSCADA.Core.VariantBase;

public class AttributesHelper
{
    @SuppressWarnings("unchecked")
    public static Map<String, Variant> fromIce ( Map attributes )
    {
        Map<String, Variant> values = new HashMap<String, Variant> ();
        
        Iterator<?> i = attributes.entrySet ().iterator ();
        while ( i.hasNext () )
        {
            Map.Entry entry = (Map.Entry)i.next ();
            String key = entry.getKey ().toString ();
            VariantBase vb = (VariantBase)entry.getValue ();
            values.put ( key, VariantHelper.fromIce ( vb ) );
        }
        
        return values;
    }
    
    public static Map<String,VariantBase> toIce ( Map<String, Variant> attribues )
    {
        Map<String, VariantBase> values = new HashMap<String, VariantBase> ();
        
        for ( Map.Entry<String, Variant> entry : attribues.entrySet () )
        {
            values.put ( entry.getKey (), VariantHelper.toIce ( entry.getValue () ) );
        }
        
        return values;
    }
}
