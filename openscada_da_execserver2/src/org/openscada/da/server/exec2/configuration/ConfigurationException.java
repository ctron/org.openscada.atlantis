package org.openscada.da.server.exec2.configuration;

import org.openscada.utils.statuscodes.CodedException;

public class ConfigurationException extends CodedException
{

    public ConfigurationException ( final String message )
    {
        super ( StatusCodes.CONFIGURATION_ERROR, message );
    }

    public ConfigurationException ( final Throwable e )
    {
        super ( StatusCodes.CONFIGURATION_ERROR, e );
    }

    public ConfigurationException ( final String message, final Throwable e )
    {
        super ( StatusCodes.CONFIGURATION_ERROR, message, e );
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5182346209934896403L;

}
