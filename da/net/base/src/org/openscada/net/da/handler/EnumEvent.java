package org.openscada.net.da.handler;

import java.util.Collection;
import java.util.List;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;

public class EnumEvent
{
    
    public static Message create ( Collection<String> added, Collection<String> removed, boolean initial )
    {
        Message msg = new Message ( Messages.CC_ENUM_EVENT );
        
        if ( initial )
            msg.getValues().put("initial", new StringValue(""));
        
        int i;
        
        i= 0;
        for ( String item : added )
        {
            msg.getValues().put("added-" + i, new StringValue(item) );
            i++;
        }
        
        i = 0;
        for ( String item : removed )
        {
            msg.getValues().put("removed-" + i, new StringValue(item) );
            i++;
        }
        return msg;
    }
    
    public static void parse ( Message message, List<String> added, List<String> removed, Boolean initial )
    {
        if ( message == null )
            return;
        if ( added == null )
            return;
        if ( removed == null )
            return;
        if ( initial == null )
            return;
        
        initial = message.getValues().containsKey("initial");
        
        added.clear();
        removed.clear();
        
        int i;
        
        i = 0;
        while ( message.getValues().containsKey("added-" + i) )
        {
            added.add ( message.getValues().get("added-" + i ).toString() );
            i++;
        }
        
        i = 0;
        while ( message.getValues().containsKey("removed-" + i) )
        {
            added.add ( message.getValues().get("removed-" + i ).toString() );
            i++;
        }
    }
}
