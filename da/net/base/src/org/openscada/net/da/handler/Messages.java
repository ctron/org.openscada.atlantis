package org.openscada.net.da.handler;

import java.util.Map;
import java.util.Properties;

import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class Messages
{
	public final static int CC_CREATE_SESSION =    0x00010001;
	public final static int CC_CLOSE_SESSION =     0x00010002;
    public final static int CC_SUBSCRIBE_ITEM =    0x00010010;
    public final static int CC_UNSUBSCRIBE_ITEM =  0x00010011;
    public final static int CC_NOTIFY_VALUE =      0x00010020;
    public final static int CC_NOTIFY_ATTRIBUTES = 0x00010021;
    
    public final static int CC_ENUM_SUBSCRIBE =    0x00010101;
    public final static int CC_ENUM_EVENT =        0x00010102;
    
    public static Message createSession ( Properties props )
    {
        Message msg = new Message ( CC_CREATE_SESSION );
        
        for ( Map.Entry<Object,Object> entry : props.entrySet() )
        {
            msg.getValues().put (entry.getKey().toString(), new StringValue(entry.getValue().toString()) );
        }
        
        return msg;
    }
    
    public static Message closeSession ()
    {
        return new Message ( CC_CLOSE_SESSION );
    }
    
    public static Message subscribeItem ( String itemName )
    {
        Message msg = new Message ( CC_SUBSCRIBE_ITEM );
        msg.getValues().put ( "item-name", new StringValue(itemName) );
        return msg;
    }
    
    public static Message unsubscribeItem ( String itemName )
    {
        Message msg = new Message ( CC_UNSUBSCRIBE_ITEM );
        msg.getValues().put ( "item-name", new StringValue(itemName) );
        return msg;
    }
    
    private static Value variantToValue ( Variant value )
    {
        if ( value == null )
            return null;
        
        try {
            if ( value.isDouble() )
                return new DoubleValue(value.asDouble());
            else if ( value.isInteger() )
                return new LongValue(value.asInteger());
            else if ( value.isLong() )
                return new LongValue(value.asLong());
            else if ( value.isString() )
                return new StringValue(value.asString());
        }
        catch ( NullValueException e )
        {
        }
        catch ( NotConvertableException e )
        {
        }
        return null;
    }
    
    public static Message notifyValue ( String itemName, Variant value, boolean initial )
    {
        Message msg = new Message ( CC_NOTIFY_VALUE );
        
        msg.getValues().put ( "item-name", new StringValue(itemName) );
        
        // flag if initial bit is set
        if ( initial )
            msg.getValues().put ( "initial", new StringValue("") );
        
        Value messageValue = variantToValue ( value );
        if ( messageValue != null )
            msg.getValues().put("value", messageValue );
        
        return msg;
    }
    
    public static Message notifyAttributes ( String itemName, Map<String,Variant> attributes, boolean initial )
    {
        Message msg = new Message ( CC_NOTIFY_ATTRIBUTES );
        
        msg.getValues().put ( "item-name", new StringValue(itemName) );
        
        // flag if initial bit is set
        if ( initial )
            msg.getValues().put ( "initial", new StringValue("") );
        
        for ( Map.Entry<String,Variant> entry : attributes.entrySet() )
        {
            
            if ( entry.getValue() == null )
            {
                msg.getValues().put("unset-" + entry.getKey(), new StringValue("") );
            }
            else if ( entry.getValue().isNull() )
            {
                msg.getValues().put("null-" + entry.getKey(), new StringValue("") );
            }
            else
            {
                Value value = variantToValue(entry.getValue());
                if ( value != null )
                {
                    msg.getValues().put("set-" + entry.getKey(), value );
                }
            }
        }
        
        return msg;
    }
}
