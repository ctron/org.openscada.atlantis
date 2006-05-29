package org.openscada.da.client.net;

public class DisconnectReason extends Exception
{

    private String _reason = "";
    
    /**
     * 
     */
    private static final long serialVersionUID = -4831595156582204891L;
    
    public DisconnectReason ()
    {
        super ( "Disconnected" );
    }
    
    public DisconnectReason ( String reason )
    {
        super ( "Disconnected: " + reason );
        _reason = reason;
    }

    public String getReason ()
    {
        return _reason;
    }
}
