package org.openscada.da.client.net;

public class OperationTimedOutException extends Exception
{

    private String _additionalInformation = "";
    
    /**
     * 
     */
    private static final long serialVersionUID = 4454590786147475753L;

    public OperationTimedOutException ()
    {
        super ( "Operation timed out" );
    }
    
    public OperationTimedOutException ( String additionalInformation )
    {
        super ( "Operation timed out: " + additionalInformation );
        _additionalInformation = additionalInformation;
    }

    public String getAdditionalInformation ()
    {
        return _additionalInformation;
    }
}
