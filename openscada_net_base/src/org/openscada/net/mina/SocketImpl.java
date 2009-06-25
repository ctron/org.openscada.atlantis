/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.mina;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.UnresolvedAddressException;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.transport.socket.apr.AprSocketAcceptor;
import org.apache.mina.transport.socket.apr.AprSocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.transport.vmpipe.VmPipeAcceptor;
import org.apache.mina.transport.vmpipe.VmPipeAddress;
import org.apache.mina.transport.vmpipe.VmPipeConnector;

public enum SocketImpl
{
    APR
    {
        @Override
        public SocketAddress doLookup ( final String address, final int port )
        {
            return NIO.doLookup ( address, port );
        }

        @Override
        public IoConnector createConnector ()
        {
            return new AprSocketConnector ();
        }

        @Override
        public IoAcceptor createAcceptor ()
        {
            final AprSocketAcceptor acceptor = new AprSocketAcceptor ();
            acceptor.setReuseAddress ( true );
            return acceptor;
        }
    },
    NIO
    {
        @Override
        public SocketAddress doLookup ( final String address, final int port )
        {
            final InetSocketAddress resolvedAddress;
            if ( address == null )
            {
                resolvedAddress = new InetSocketAddress ( port );
            }
            else
            {
                resolvedAddress = new InetSocketAddress ( address, port );
            }

            if ( resolvedAddress.isUnresolved () )
            {
                throw new UnresolvedAddressException ();
            }

            return resolvedAddress;
        }

        @Override
        public IoConnector createConnector ()
        {
            return new NioSocketConnector ();
        }

        @Override
        public IoAcceptor createAcceptor ()
        {
            final NioSocketAcceptor acceptor = new NioSocketAcceptor ();
            acceptor.setReuseAddress ( true );
            return acceptor;
        }
    },
    VMPIPE
    {
        @Override
        public SocketAddress doLookup ( final String address, final int port )
        {
            return new VmPipeAddress ( port );
        }

        @Override
        public IoConnector createConnector ()
        {
            return new VmPipeConnector ();
        }

        @Override
        public IoAcceptor createAcceptor ()
        {
            return new VmPipeAcceptor ();
        }
    };

    public static SocketImpl fromName ( final String name )
    {
        if ( name == null )
        {
            return NIO;
        }

        try
        {
            final SocketImpl impl = SocketImpl.valueOf ( name );
            if ( impl != null )
            {
                return impl;
            }
        }
        catch ( final Throwable e )
        {

        }

        // check defined aliases
        if ( "apr".equals ( name ) )
        {
            return APR;
        }
        if ( "vm".equals ( name ) )
        {
            return VMPIPE;
        }
        if ( "vmpipe".equals ( name ) )
        {
            return VMPIPE;
        }
        return NIO;
    }

    public abstract SocketAddress doLookup ( String address, int port );

    public abstract IoConnector createConnector ();

    public abstract IoAcceptor createAcceptor ();
}
