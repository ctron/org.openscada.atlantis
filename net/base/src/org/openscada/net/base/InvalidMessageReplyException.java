package org.openscada.net.base;

public class InvalidMessageReplyException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 5383513193481537280L;

    public InvalidMessageReplyException ()
    {
        super ( "Invalid message reply" );
    }
    
    public InvalidMessageReplyException ( String message )
    {
        super ( "Invalid message reply: " + message );
    }
}
