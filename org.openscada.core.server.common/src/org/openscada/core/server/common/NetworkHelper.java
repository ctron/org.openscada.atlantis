/*
 * This file is part of the OpenSCADA project
 * 
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

package org.openscada.core.server.common;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.service.IoAcceptor;
import org.openscada.core.ConnectionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkHelper
{

    private final static Logger logger = LoggerFactory.getLogger ( NetworkHelper.class );

    public static Set<ConnectionInformation> getLocalInterfaces ( final IoAcceptor acceptor, final ConnectionInformation connectionInformation ) throws SocketException
    {
        final Set<ConnectionInformation> result = new HashSet<ConnectionInformation> ();

        for ( final InetSocketAddress address : getLocalAddresses ( acceptor ) )
        {
            result.add ( cloneFill ( address.getAddress (), connectionInformation ) );
        }

        return result;
    }

    public static Set<InetSocketAddress> getLocalAddresses ( final IoAcceptor acceptor ) throws SocketException
    {
        final Set<InetSocketAddress> result = new HashSet<InetSocketAddress> ();

        for ( final SocketAddress address : acceptor.getLocalAddresses () )
        {
            logger.info ( "Bound to: {}", address );
            if ( ! ( address instanceof InetSocketAddress ) )
            {
                continue;
            }

            final InetSocketAddress socketAddress = (InetSocketAddress)address;
            if ( socketAddress.getAddress ().isAnyLocalAddress () )
            {
                final int port = socketAddress.getPort ();

                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces ();
                while ( interfaces.hasMoreElements () )
                {
                    final NetworkInterface networkInterface = interfaces.nextElement ();

                    for ( final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses () )
                    {
                        result.add ( new InetSocketAddress ( interfaceAddress.getAddress (), port ) );
                    }
                }
            }
            else
            {
                result.add ( socketAddress );
            }

        }

        return result;
    }

    private static ConnectionInformation cloneFill ( final InetAddress inetAddress, final ConnectionInformation connectionInformation )
    {
        final ConnectionInformation info = connectionInformation.clone ();
        info.setTarget ( inetAddress.getHostAddress () );
        return info;
    }

}
