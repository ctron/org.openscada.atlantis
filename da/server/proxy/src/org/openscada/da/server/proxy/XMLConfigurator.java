package org.openscada.da.server.proxy;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;
import org.openscada.da.proxy.configuration.ProxyType;
import org.openscada.da.proxy.configuration.RedundantType;
import org.openscada.da.proxy.configuration.RootDocument;
import org.openscada.da.proxy.configuration.SubConnectionType;

public class XMLConfigurator
{
    private final RootDocument document;

    final Map<String, Connection> connections = new HashMap<String, Connection> ();

    public XMLConfigurator ( final RootDocument document )
    {
        this.document = document;
    }

    public void configure ( final Hive hive ) throws ClassNotFoundException
    {
        final String separator = this.document.getRoot ().getSeparator ();
        hive.setSeparator ( separator );
        for ( final ProxyType con : this.document.getRoot ().getProxyList () )
        {
            final String serverPrefix = con.getPrefix ();
            final String uri = con.getConnection ().getUri ();
            final String className = con.getConnection ().getClassName ();
            final Connection connection = makeConnection ( uri, className );
            connection.connect ();
            hive.addConnection ( connection, serverPrefix );
        }
        for ( final RedundantType con : this.document.getRoot ().getRedundantList () )
        {
            final String serverPrefix = con.getPrefix ();
            final RedundantConnection redundantConnection = new RedundantConnection ( separator, serverPrefix );
            for ( final SubConnectionType subConnection : con.getConnectionList () )
            {
                final String id = subConnection.getId ();
                final String uri = subConnection.getUri ();
                final String className = subConnection.getClassName ();
                final String remotePrefix = subConnection.getPrefix ();
                redundantConnection.addConnection ( makeConnection ( uri, className ), id, remotePrefix );
            }
            redundantConnection.connect ();
            hive.addConnection ( redundantConnection );
        }
        hive.initialize ();
    }

    private Connection makeConnection ( final String uri, final String className ) throws ClassNotFoundException
    {
        Connection connection = this.connections.get ( uri );
        if ( connection == null )
        {
            if ( ( className != null ) && ( className.length () > 0 ) )
            {
                Class.forName ( className );
            }
            connection = (Connection)ConnectionFactory.create ( ConnectionInformation.fromURI ( uri ) );
            this.connections.put ( uri, connection );
        }
        return connection;
    }
}
