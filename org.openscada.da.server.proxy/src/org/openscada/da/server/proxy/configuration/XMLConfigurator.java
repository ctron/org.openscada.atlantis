/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.proxy.configuration;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;
import org.openscada.da.proxy.configuration.ConnectionType;
import org.openscada.da.proxy.configuration.ProxyType;
import org.openscada.da.proxy.configuration.RootDocument;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.proxy.Hive;
import org.openscada.da.server.proxy.connection.ProxyConnection;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class XMLConfigurator
{
    private final RootDocument document;

    final Map<String, Connection> connections = new HashMap<String, Connection> ();

    /**
     * @param document
     */
    public XMLConfigurator ( final RootDocument document )
    {
        this.document = document;
    }

    /**
     * @param hive
     * @throws ClassNotFoundException
     * @throws InvalidOperationException
     * @throws NullValueException
     * @throws NotConvertableException
     * @throws ConfigurationError 
     */
    public void configure ( final Hive hive ) throws ClassNotFoundException, InvalidOperationException, NullValueException, NotConvertableException, ConfigurationError
    {
        // first configure the base hive
        new org.openscada.da.server.common.configuration.xml.XMLConfigurator ( null, this.document.getRoot ().getItemTemplates (), null, null ).configure ( hive );
        // then the rest of the hive
        if ( this.document.getRoot ().isSetSeparator () )
        {
            hive.setSeparator ( this.document.getRoot ().getSeparator () );
        }
        for ( final ProxyType proxyConf : this.document.getRoot ().getProxyList () )
        {
            final ProxyConnection proxyConnection = hive.addConnection ( new ProxyPrefixName ( proxyConf.getPrefix () ) );
            proxyConnection.setWait ( proxyConf.getWait () );

            for ( final ConnectionType connectionConf : proxyConf.getConnectionList () )
            {
                final Connection connection = createConnection ( connectionConf.getUri (), connectionConf.getClassName () );
                proxyConnection.addConnection ( connection, connectionConf.getId (), new ProxyPrefixName ( connectionConf.getPrefix () ) );
            }
        }
    }

    /**
     * @param uri
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Connection createConnection ( final String uri, final String className ) throws ClassNotFoundException
    {
        Connection connection = this.connections.get ( uri );
        if ( connection == null )
        {
            if ( className != null && className.length () > 0 )
            {
                Class.forName ( className );
            }
            connection = (Connection)ConnectionFactory.create ( ConnectionInformation.fromURI ( uri ) );
            this.connections.put ( uri, connection );
        }
        return connection;
    }
}
