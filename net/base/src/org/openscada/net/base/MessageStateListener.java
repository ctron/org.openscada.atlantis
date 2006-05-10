package org.openscada.net.base;

import org.openscada.net.base.data.Message;

public interface MessageStateListener
{
    public void messageComplete ( Message message );
    public void messageTimedOut ( );
}
