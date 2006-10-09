package org.openscada.da.client.viewer.configurator;

public class ConfigurationError extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -6219963055068383041L;
    
    public ConfigurationError ()
    {
        super ();
    }
    
    public ConfigurationError ( String message )
    {
        super ( message );
    }
    
    public ConfigurationError ( String message, Throwable cause )
    {
        super ( message, cause );
    }

}
