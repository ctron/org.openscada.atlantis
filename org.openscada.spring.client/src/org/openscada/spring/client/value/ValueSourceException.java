package org.openscada.spring.client.value;

public class ValueSourceException extends Exception
{

    private static final long serialVersionUID = 1L;

    public ValueSourceException ( final String message )
    {
        super ( message );
    }

    public ValueSourceException ( final Throwable cause )
    {
        super ( cause );
    }

    public ValueSourceException ( final String message, final Throwable cause )
    {
        super ( message, cause );
    }
}
