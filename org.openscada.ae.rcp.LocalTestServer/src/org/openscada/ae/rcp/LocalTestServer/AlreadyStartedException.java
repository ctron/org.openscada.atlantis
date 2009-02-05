package org.openscada.ae.rcp.LocalTestServer;

public class AlreadyStartedException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -4082516254400679343L;

    public AlreadyStartedException ()
    {
        super ( "The local embedded server was already started. There can only be one instance running at a time!" );
    }
}
