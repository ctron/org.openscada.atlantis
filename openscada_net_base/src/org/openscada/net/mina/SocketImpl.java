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
        public SocketAddress doLookup ( final String address, final int port )
        {
            return NIO.doLookup ( address, port );
        }

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
        public SocketAddress doLookup ( final String address, final int port )
        {
            return new VmPipeAddress ( port );
        }

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

        final SocketImpl impl = SocketImpl.valueOf ( name );
        if ( impl != null )
        {
            return impl;
        }
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
