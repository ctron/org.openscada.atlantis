/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.server.ngp.test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.openscada.core.server.ngp.ServerBase;
import org.openscada.core.server.ngp.ServerConnection;
import org.openscada.protocol.ngp.common.ProtocolConfiguration;
import org.openscada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.openscada.protocol.ngp.common.SslHelper;

public class ServerTest1 implements IApplication
{

    private ServerBase server;

    private static ProtocolConfiguration makeProtocolConfiguration () throws Exception
    {
        final ProtocolConfiguration protocolConfiguration = new ProtocolConfiguration ( ServerTest1.class.getClassLoader () );
        protocolConfiguration.setSslContextFactory ( makeSslContextFactory ( System.getProperties () ) );
        return protocolConfiguration;
    }

    private static SslContextFactory makeSslContextFactory ( final Properties properties ) throws Exception
    {
        final Map<String, String> p = new HashMap<String, String> ();
        for ( final Map.Entry<Object, Object> entry : properties.entrySet () )
        {
            p.put ( "" + entry.getKey (), "" + entry.getValue () );
        }
        return SslHelper.createDefaultSslFactory ( p, false );
    }

    @Override
    public Object start ( final IApplicationContext context ) throws Exception
    {
        final ProtocolConfigurationFactory factory = new ProtocolConfigurationFactory () {
            @Override
            public ProtocolConfiguration createConfiguration ( final boolean clientMode ) throws Exception
            {
                return makeProtocolConfiguration ();
            }
        };

        this.server = new ServerBase ( Arrays.asList ( new InetSocketAddress ( 1202 ) ), factory ) {
            @Override
            public org.openscada.core.server.ngp.ServerConnection createNewConnection ( final IoSession session )
            {
                return new ServerConnection ( session ) {

                    @Override
                    public void messageReceived ( final Object message )
                    {
                        System.out.println ( "Message received: " + message );
                    }
                };
            };
        };
        this.server.start ();
        return null;
    }

    @Override
    public void stop ()
    {
        this.server.dispose ();
    }

}
