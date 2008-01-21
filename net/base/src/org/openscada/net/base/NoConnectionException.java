package org.openscada.net.base;

public class NoConnectionException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -5722669689966000196L;

    public NoConnectionException ()
    {
        super ( "No connection" );
    }
}
