package org.openscada.net.da.handler;

import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.utils.lang.Holder;

public class WriteOperation
{

    public static Message create ( String itemName, Variant value )
    {
        Message message = new Message ( Messages.CC_WRITE_OPERATION );
        
        message.getValues().put ( "item-name", new StringValue(itemName) );
        message.getValues().put ( "value", Messages.variantToValue(value) );
        
        return message;
    }

    public static void parse ( Message message, Holder<String> itemName, Holder<Variant> value )
    {
        // FIXME: handle missing item name
        itemName.value = message.getValues().get ( "item-name" ).toString();
        
        value.value = Messages.valueToVariant ( message.getValues().get("value"), new Variant() );
    }
}
