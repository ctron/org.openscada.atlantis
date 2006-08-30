package org.openscada.ae.net;

import org.openscada.net.base.data.Message;

public class ListMessage
{
    public Message toMessage ()
    {
        return new Message ( Messages.CC_LIST );
    }
    
    public static ListMessage fromMessage ( Message message )
    {
        return new ListMessage ();
    }
    
}
