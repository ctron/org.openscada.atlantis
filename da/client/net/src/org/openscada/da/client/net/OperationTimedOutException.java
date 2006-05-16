package org.openscada.da.client.net;

public class OperationTimedOutException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 4454590786147475753L;

    public OperationTimedOutException ()
    {
        super ("Operation timed out");
    }
}
