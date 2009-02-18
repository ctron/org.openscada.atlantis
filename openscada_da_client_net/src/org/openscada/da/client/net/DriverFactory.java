/**
 * 
 */
package org.openscada.da.client.net;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.DriverInformation;

public class DriverFactory implements org.openscada.core.client.DriverFactory
{
    public DriverInformation getDriverInformation ( final ConnectionInformation connectionInformation )
    {
        if ( !connectionInformation.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            return null;
        }
        if ( ! ( connectionInformation.getDriver ().equalsIgnoreCase ( "net" ) || connectionInformation.getDriver ().equalsIgnoreCase ( "gmpp" ) ) )
        {
            return null;
        }

        if ( connectionInformation.getSecondaryTarget () == null )
        {
            return null;
        }

        return new org.openscada.da.client.net.DriverInformation ();
    }

    public static void registerDriver ()
    {
        ConnectionFactory.registerDriverFactory ( new DriverFactory () );
    }
}