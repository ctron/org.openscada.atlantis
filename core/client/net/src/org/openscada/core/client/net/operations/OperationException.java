package org.openscada.core.client.net.operations;

public class OperationException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1241419652332950862L;

    public OperationException ( String message, Throwable cause )
    {
        super ( message, cause );
    }
    
    public OperationException ( String message )
    {
        super ( message );
    }
    
    public OperationException ( Throwable cause )
    {
        super ( cause );
    }
}
