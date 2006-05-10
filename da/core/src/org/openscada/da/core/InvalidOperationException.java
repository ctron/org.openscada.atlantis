package org.openscada.da.core;

public class InvalidOperationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7236076680490424429L;
	
    public InvalidOperationException ()
    {
        super("Invalid operation");
    }
}
