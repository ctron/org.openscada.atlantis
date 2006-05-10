package org.openscada.da.core;

public class InvalidSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6189065814886204302L;

    public InvalidSessionException ()
    {
        super("Invalid session");
    }
}
