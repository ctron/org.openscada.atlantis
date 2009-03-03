package org.openscada.net.mina;

import org.openscada.net.base.data.Message;

public interface MessageSender
{

    /**
     * Send a message out to somewhere
     * @param message the message to send
     * @param prepareSendHandler the prepare handler to call after the message is prepared
     * @return <code>true</code> if the message was send out (does not guarantee a successful delivery!)
     */
    public boolean sendMessage ( Message message, PrepareSendHandler prepareSendHandler );

    /**
     * Close the session of the sender
     */
    public void close ();
}
