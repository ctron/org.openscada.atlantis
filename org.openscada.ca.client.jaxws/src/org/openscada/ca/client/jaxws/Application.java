package org.openscada.ca.client.jaxws;

import java.net.MalformedURLException;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.FactoryInformation;
import org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator;

public class Application
{

    public static void main ( final String[] args ) throws MalformedURLException
    {
        final RemoteConfigurationClient client = new RemoteConfigurationClient ( "localhost", 9999 );

        final RemoteConfigurationAdministrator port = client.getPort ();

        System.out.println ( "HasService: " + port.hasService () );

        System.out.println ( "Start request" );

        for ( final FactoryInformation factory : port.getFactories () )
        {
            System.out.println ( String.format ( "FactoryInformation: %s", factory.getId () ) );
            final FactoryInformation data = port.getFactory ( factory.getId () );
            for ( final ConfigurationInformation configuration : data.getConfigurations () )
            {
                System.out.println ( configuration.getId () + " -> " + configuration.getData () );
            }
        }

        System.out.println ( "End request" );
    }
}
