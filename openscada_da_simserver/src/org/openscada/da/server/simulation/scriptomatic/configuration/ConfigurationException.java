package org.openscada.da.server.simulation.scriptomatic.configuration;

import org.openscada.utils.statuscodes.CodedException;
import org.openscada.utils.statuscodes.StatusCode;

public class ConfigurationException extends CodedException
{

    public ConfigurationException ( final StatusCode statusCode, final Throwable cause )
    {
        super ( statusCode, cause );
    }

    /**
     * 
     */
    private static final long serialVersionUID = 2772870710621636603L;

}
