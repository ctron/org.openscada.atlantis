package org.openscada.da.core.common.configuration.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.common.AttributeType;
import org.openscada.common.AttributesType;
import org.openscada.common.VariantType;
import org.openscada.core.Variant;
import org.openscada.da.core.common.configuration.ConfigurationError;

public class Helper
{
    public static Variant fromXML ( VariantType variantType )
    {
        if ( variantType.getBoolean () != null )
        {
            return new Variant ( variantType.getBoolean ().getBooleanValue () );
        }
        else if ( variantType.getDouble () != null )
        {
            return new Variant ( variantType.getDouble ().getDoubleValue () );
        }
        else if ( variantType.getInt32 () != null )
        {
            return new Variant ( variantType.getInt32 ().getIntValue () );
        }
        else if ( variantType.getInt64 () != null )
        {
            return new Variant ( variantType.getInt64 ().getLongValue () );
        }
        else if ( variantType.getString () != null )
        {
            return new Variant ( variantType.getString () );
        }
        else if ( variantType.getNull () != null )
        {
            return new Variant ();
        }
        else
        {
            return null;
        }
    }
    
    public static Map<String, Variant> convertAttributes ( AttributesType attributes ) throws ConfigurationError
    {
        Map<String, Variant> attributesMap = new HashMap<String, Variant> ();
        
        if ( attributes == null )
            return attributesMap;
        if ( attributes.getAttributeList () == null )
            return attributesMap;
        
        for ( AttributeType attribute : attributes.getAttributeList () )
        {
            String key = attribute.getName ();
            
            Variant value = fromXML ( attribute );
            if ( value == null )
                throw new ConfigurationError ( "Invalid variant value configuration" );
            
            attributesMap.put ( key, value );
        }
        
        return attributesMap;
    }
}
