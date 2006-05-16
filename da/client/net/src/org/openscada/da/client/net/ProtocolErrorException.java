package org.openscada.da.client.net;

public class ProtocolErrorException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 7382933809822999189L;

    public ProtocolErrorException ( )
    {
        super ( "Protocol error" );
    }
}
