package org.openscada.ae.net;

import org.openscada.net.base.data.Message;

public class CloseSessionMessage
{
    public Message toMessage ()
    {
        return new Message ( Messages.CC_CLOSE_SESSION );
    }
    
    public static CloseSessionMessage fromMessage ( Message message )
    {
        return new CloseSessionMessage ();
    }
}
