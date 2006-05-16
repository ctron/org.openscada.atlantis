package org.openscada.net.base;

import org.openscada.net.base.data.Message;

public interface MessageStateListener
{
    public void messageReply ( Message message );
    public void messageTimedOut ( );
}
