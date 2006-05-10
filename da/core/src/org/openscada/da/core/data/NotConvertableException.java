package org.openscada.da.core.data;

public class NotConvertableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1763267117219727670L;

    public NotConvertableException ()
    {
        super("Value not convertable");
    }
}
