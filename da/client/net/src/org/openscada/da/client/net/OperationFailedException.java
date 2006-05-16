package org.openscada.da.client.net;

public class OperationFailedException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = -6829837359703669899L;

    public OperationFailedException ( String failure )
    {
        super ( failure );
    }
}
