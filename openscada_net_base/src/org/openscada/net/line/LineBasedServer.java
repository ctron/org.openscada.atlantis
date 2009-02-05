/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.net.line;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.ServerSocket;
import org.openscada.net.io.SocketConnection;
import org.openscada.net.io.ServerSocket.ConnectionFactory;

public class LineBasedServer
{
    public interface HandlerFactory
    {
        LineHandler createHandler ();
    }

    private HandlerFactory _factory = null;

    @SuppressWarnings ( "unused" )
    private ServerSocket _server = null;

    public LineBasedServer ( IOProcessor processor, int port, HandlerFactory factory ) throws IOException
    {
        _factory = factory;

        _server = new ServerSocket ( processor, new InetSocketAddress ( port ), new ConnectionFactory () {

            public void accepted ( SocketConnection connection )
            {
                if ( _factory != null )
                {
                    LineBasedConnection newConnection = new LineBasedConnection ( connection, _factory.createHandler () );
                    newConnection.connected ();
                }
            }
        } );
    }
}
