package org.openscada.ca.client.jaxws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator;

public class RemoteConfigurationClient
{
    private static final QName serviceName = QName.valueOf ( "{http://jaxws.servelt.ca.openscada.org/}ConfigurationAdministratorServiceService" );

    private final RemoteConfigurationAdministrator port;

    public RemoteConfigurationClient ( final String hostname, final int port ) throws MalformedURLException
    {
        this ( new URL ( String.format ( "http://%s:%s/org.openscada.ca.servlet.jaxws?WSDL", hostname, port ) ), serviceName );
    }

    public RemoteConfigurationClient ( final String url ) throws MalformedURLException
    {
        this ( new URL ( url ), serviceName );
    }

    public RemoteConfigurationClient ( final String url, final String serviceName ) throws MalformedURLException
    {
        this ( new URL ( url ), QName.valueOf ( serviceName ) );
    }

    public RemoteConfigurationClient () throws MalformedURLException
    {
        this ( new URL ( "http://localhost:9999/org.openscada.ca.servlet.jaxws?WSDL" ), serviceName );
    }

    public RemoteConfigurationClient ( final URL url, final QName serviceName )
    {
        final Service service = javax.xml.ws.Service.create ( url, serviceName );

        this.port = service.getPort ( RemoteConfigurationAdministrator.class );
    }

    public RemoteConfigurationAdministrator getPort ()
    {
        return this.port;
    }
}
