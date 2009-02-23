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

package org.openscada.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.line.LineBasedClient;
import org.openscada.net.line.LineBasedServer;
import org.openscada.net.line.LineHandler;

public class LineApplication
{
    public static void main ( String[] args ) throws IOException
    {
        IOProcessor io = new IOProcessor ();
        new LineBasedServer ( io, 1202, new LineBasedServer.HandlerFactory () {

            public LineHandler createHandler ()
            {
                return new LineHandlerTestImpl ( true );
            }
        } );

        final LineBasedClient client = new LineBasedClient ( io, new LineHandlerTestImpl ( false ) );

        io.getScheduler ().executeJobAsync ( new Runnable () {

            public void run ()
            {
                client.connect ( new InetSocketAddress ( "localhost", 1202 ) );
            }
        } );

        io.run ();
    }
}
