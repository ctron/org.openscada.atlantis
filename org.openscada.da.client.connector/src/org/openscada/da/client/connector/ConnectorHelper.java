package org.openscada.da.client.connector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;

/**
 * This helper class aids in finding and creating a new connection or driver adapter 
 * @author Jens Reimann
 *
 */
public class ConnectorHelper
{

    public static DriverAdapter findDriverAdapter ( final ConnectionInformation connectionInformation )
    {
        final DriverAdapterInformation dai = findDriverAdapterInformation ( connectionInformation );
        if ( dai == null )
        {
            return null;
        }
        return dai.createDriverAdapter ();
    }

    public static DriverAdapterInformation findDriverAdapterInformation ( final ConnectionInformation connectionInformation )
    {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry ().getConfigurationElementsFor ( "org.openscada.da.client.connector" );

        for ( final IConfigurationElement element : elements )
        {
            final String name = element.getName ();
            if ( !"driverAdapter".equals ( name ) )
            {
                continue;
            }
            final String interfaceString = element.getAttribute ( "interface" );
            final String typeString = element.getAttribute ( "type" );

            if ( connectionInformation.getInterface ().equals ( interfaceString ) )
            {
                if ( connectionInformation.getDriver ().equals ( typeString ) )
                {
                    return new DriverAdapterInformation ( element );
                }
            }

        }
        return null;
    }

    public static Connection createConnection ( final ConnectionInformation connectionInformation )
    {
        final DriverAdapter adapter = findDriverAdapter ( connectionInformation );
        if ( adapter == null )
        {
            return null;
        }
        return adapter.createConnection ( connectionInformation );
    }
}
