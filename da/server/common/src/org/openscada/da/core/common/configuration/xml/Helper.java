package org.openscada.da.core.common.configuration.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.common.AttributeType;
import org.openscada.common.AttributesType;
import org.openscada.core.Variant;
import org.openscada.da.core.common.configuration.ConfigurationError;

public class Helper
{
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
            Variant value = null;
            
            if ( attribute.getBoolean () != null )
            {
                value = new Variant ( attribute.getBoolean ().getBooleanValue () );
            }
            else if ( attribute.getDouble () != null )
            {
                value = new Variant ( attribute.getDouble ().getDoubleValue () );
            }
            else if ( attribute.getInt32 () != null )
            {
                value = new Variant ( attribute.getInt32 ().getIntValue () );
            }
            else if ( attribute.getInt64 () != null )
            {
                value = new Variant ( attribute.getInt64 ().getLongValue () );
            }
            else if ( attribute.getString () != null )
            {
                value = new Variant ( attribute.getString () );
            }
            else if ( attribute.getNull () != null )
            {
                value = new Variant ();
            }
            else
            {
                throw new ConfigurationError ( "Invalid variant value configuration" );
            }
            
            attributesMap.put ( key, value );
        }
        
        return attributesMap;
    }
}
