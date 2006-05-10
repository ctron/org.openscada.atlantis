package org.openscada.da.core.data;

public class NullValueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -198014812983830196L;

    public NullValueException ()
    {
        super("Null value");
    }
}
