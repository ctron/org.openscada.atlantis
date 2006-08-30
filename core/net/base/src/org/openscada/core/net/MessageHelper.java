package org.openscada.core.net;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.net.base.data.BooleanValue;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class MessageHelper
{
    /**
     * Convert a MapValue to a attributes map
     * @param mapValue the map value to convert
     * @return the attributes map
     * @note Only scalar entries in the map are converted. Other values are skipped.
     */
    public static Map<String, Variant> mapToAttributes ( MapValue mapValue )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        for ( Map.Entry<String, Value> entry : mapValue.getValues ().entrySet () )
        {
            Variant value = null;
            Value entryValue = entry.getValue ();
            
            value = valueToVariant ( entryValue, null );
            
            if ( value != null )
            {
                attributes.put ( new String ( entry.getKey () ) , value );
            }
        }
        
        return attributes;
    }
    
    public static MapValue attributesToMap ( Map<String, Variant> attributes )
    {
        MapValue mapValue = new MapValue ();
        
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            Value value = variantToValue ( entry.getValue () );
            if ( value != null )
            {
                mapValue.put ( new String ( entry.getKey () ), value );
            }
        }
        
        return mapValue;
    }
    

    public static Variant valueToVariant ( Value value, Variant defaultValue )
    {
        if ( value == null )
            return defaultValue;
        
        if ( value instanceof StringValue )
            return new Variant ( ((StringValue)value).getValue () );
        else if ( value instanceof BooleanValue )
            return new Variant ( ((BooleanValue)value).getValue () );
        else if ( value instanceof DoubleValue )
            return new Variant ( ((DoubleValue)value).getValue () );
        else if ( value instanceof LongValue )
            return new Variant ( ((LongValue)value).getValue () );
        else if ( value instanceof IntegerValue )
            return new Variant ( ((IntegerValue)value).getValue () );
        else if ( value instanceof VoidValue )
            return new Variant ();
        
        return defaultValue;
    }
    
    public static Value variantToValue ( Variant value )
    {
        if ( value == null )
            return null;
        
        try {
            if ( value.isDouble () )
                return new DoubleValue ( value.asDouble () );
            else if ( value.isInteger () )
                return new IntegerValue ( value.asInteger () );
            else if ( value.isLong () )
                return new LongValue ( value.asLong () );
            else if ( value.isBoolean () )
                return new BooleanValue ( value.asBoolean () );
            else if ( value.isString () )
                return new StringValue ( value.asString () );
            else if ( value.isNull () )
                return new VoidValue ();
        }
        catch ( NullValueException e )
        {
            return new VoidValue ();
        }
        catch ( NotConvertableException e )
        {
        }
        return null;
    }
    
}
