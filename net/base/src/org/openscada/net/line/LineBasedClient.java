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
import java.net.SocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.SocketConnection;

public class LineBasedClient implements LineHandler
{
    private LineBasedConnection _connection = null;

    private IOProcessor _processor = null;
    private LineHandler _handler = null;

    public LineBasedClient ( IOProcessor processor, LineHandler handler )
    {
        _processor = processor;
        _handler = handler;
    }

    public void connect ( SocketAddress remote )
    {
        synchronized ( this )
        {
            if ( _connection != null )
                return;
            try
            {
                setConnection ( new LineBasedConnection ( new SocketConnection ( _processor ), this ) );
                _connection.getConnection ().connect ( remote );
            }
            catch ( IOException e )
            {
                connectionFailed ( e );
            }
        }
    }

    public void handleLine ( String line )
    {
        _handler.handleLine ( line );
    }

    public void setConnection ( LineBasedConnection connection )
    {
        _connection = connection;
        _handler.setConnection ( connection );
    }

    public void closed ()
    {
        setConnection ( null );
        _handler.closed ();
    }

    public void connected ()
    {
        _handler.connected ();
    }

    public void connectionFailed ( Throwable throwable )
    {
        _handler.connectionFailed ( throwable );
        setConnection ( null );
    }
}
