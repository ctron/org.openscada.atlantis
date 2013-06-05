/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
import org.openscada.da.proxy.configuration.RootType;
import org.openscada.da.server.proxy.Hive;
import org.openscada.da.server.proxy.connection.ProxyConnection;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
 */
public class XMLConfigurator
{
    private final RootType root;

    private final Map<String, Connection> connections = new HashMap<String, Connection> ();

    /**
     * @param document
     */
    public XMLConfigurator ( final RootType root )
    {
        this.root = root;
    }

    /**
     * @param hive
     * @throws ClassNotFoundException
     * @throws InvalidOperationException
     * @throws NullValueException
     * @throws NotConvertableException
     * @throws ConfigurationError
     */
    public void configure ( final Hive hive ) throws ClassNotFoundException, InvalidOperationException, NullValueException, NotConvertableException
    {
        if ( this.root.isSetSeparator () )
        {
            hive.setSeparator ( this.root.getSeparator () );
        }
        for ( final ProxyType proxyConf : this.root.getProxy () )
        {
            final ProxyConnection proxyConnection = hive.addConnection ( new ProxyPrefixName ( proxyConf.getPrefix () ) );
            proxyConnection.setWait ( proxyConf.getWait () );

            for ( final ConnectionType connectionConf : proxyConf.getConnection () )
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
