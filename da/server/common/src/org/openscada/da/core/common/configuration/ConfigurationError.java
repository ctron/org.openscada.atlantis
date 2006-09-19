package org.openscada.da.core.common.configuration;

public class ConfigurationError extends Exception
{

    public ConfigurationError ( String message )
    {
        super ( message );
    }

    public ConfigurationError ( String message, Throwable cause )
    {
        super ( message, cause );
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8129434642805635897L;

}
