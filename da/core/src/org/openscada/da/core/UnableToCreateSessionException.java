package org.openscada.da.core;

public class UnableToCreateSessionException extends Exception
{

    private String _reason = "";
    
    /**
     * 
     */
    private static final long serialVersionUID = -3211208282862897815L;

    public UnableToCreateSessionException ( String reason )
    {
        super ( "Unable to create session: " + reason );
        _reason = reason;
    }

    public String getReason ()
    {
        return _reason;
    }
}
