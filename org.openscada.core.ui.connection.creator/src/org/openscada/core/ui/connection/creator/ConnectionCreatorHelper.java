package org.openscada.core.ui.connection.creator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionService;

public class ConnectionCreatorHelper
{
    public static ConnectionService createConnection ( final ConnectionInformation info, final Integer autoReconnectDelay )
    {
        if ( info == null )
        {
            return null;
        }

        for ( final IConfigurationElement ele : Platform.getExtensionRegistry ().getConfigurationElementsFor ( Activator.EXTP_CONNECTON_CREATOR ) )
        {
            final String interfaceName = ele.getAttribute ( "interface" );
            final String driverName = ele.getAttribute ( "driver" );
            if ( interfaceName == null || driverName == null )
            {
                continue;
            }
            if ( interfaceName.equals ( info.getInterface () ) && driverName.equals ( info.getDriver () ) )
            {
                final ConnectionService service = createConnection ( info, ele, autoReconnectDelay );
                if ( service != null )
                {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Create a new connection from the connection created defined in the element
     * @param connectionInformation the connection information
     * @param ele the configuration element
     * @param autoReconnectDelay the automatic reconnect delay or <code>null</code> if not
     * automatic reconnect should be used
     * @return a new {@link ConnectionService} or <code>null</code>
     */
    private static ConnectionService createConnection ( final ConnectionInformation connectionInformation, final IConfigurationElement ele, final Integer autoReconnectDelay )
    {
        try
        {
            final Object o = ele.createExecutableExtension ( "class" );
            if ( ! ( o instanceof ConnectionCreator ) )
            {
                return null;
            }

            return ( (ConnectionCreator)o ).createConnection ( connectionInformation, autoReconnectDelay );
        }
        catch ( final CoreException e )
        {
            Activator.getDefault ().getLog ().log ( e.getStatus () );
            return null;
        }
    }
}
