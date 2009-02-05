package org.openscada.net.base;

public class MessageTimeoutException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -3713171857307738116L;

    public MessageTimeoutException ()
    {
        super ( "Message timed out" );
    }
}
