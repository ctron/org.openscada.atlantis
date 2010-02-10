package org.openscada.da.server.simulation.scriptomatic.configuration;

import java.io.File;
import java.io.IOException;

import org.openscada.da.server.simulation.scriptomatic.Hive;

public class PropertyConfigurator implements Configurator
{

    public void configure ( final Hive hive ) throws ConfigurationException
    {
        final OpenOfficeLoader loader = new OpenOfficeLoader ( hive );

        try
        {
            loader.load ( new File ( System.getProperty ( "org.openscada.scriptomatic.openoffice.file" ) ) );
        }
        catch ( final IOException e )
        {
            throw new ConfigurationException ( StatusCodes.GENERIC_ERROR, e );
        }
    }

}
