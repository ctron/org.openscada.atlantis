/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.client.ngp.test;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.common.ClientBaseConnection;
import org.openscada.core.client.ngp.ProtocolIoHandlerFactory;
import org.openscada.protocol.ngp.common.DefaultProtocolConfigurationFactory;
import org.openscada.protocol.ngp.common.FilterChainBuilder;
import org.openscada.protocol.ngp.common.ProtocolConfiguration;
import org.openscada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionTest1 implements IApplication
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionTest1.class );

    private ClientBaseConnection connection;

    @Override
    public Object start ( final IApplicationContext context ) throws Exception
    {
        final String uri = System.getProperty ( "connectionUri", "da:ngp://localhost:1202" );

        final ConnectionInformation connectionInformation = ConnectionInformation.fromURI ( uri );

        final ProtocolConfigurationFactory protocolConfigurationFactory = new DefaultProtocolConfigurationFactory ( connectionInformation ) {
            @Override
            protected void customizeConfiguration ( final ProtocolConfiguration configuration, final boolean clientMode )
            {
                addJavaProtocol ( "1", configuration, ConnectionTest1.class.getClassLoader () );
            }
        };

        logger.info ( "Start - Begin" );
        this.connection = new ClientBaseConnection ( new ProtocolIoHandlerFactory ( protocolConfigurationFactory ), new FilterChainBuilder ( true ), connectionInformation ) {
            @Override
            protected void handleMessage ( final Object message )
            {
                System.out.println ( "Received message: " + message );
            }
        };
        this.connection.addConnectionStateListener ( new DumpingStateListener () );
        this.connection.connect ();

        logger.info ( "Start - Sleep" );

        Thread.sleep ( 1000 * 1000 );

        logger.info ( "Start - End" );
        return null;
    }

    @Override
    public void stop ()
    {
        logger.info ( "Stop - Begin" );
        this.connection.dispose ();
        logger.info ( "Stop - End" );
    }

}
