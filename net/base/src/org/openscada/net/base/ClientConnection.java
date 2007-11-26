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

package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.net.Client;

public class ClientConnection extends ConnectionHandlerBase
{
    private static Logger _log = Logger.getLogger ( ClientConnection.class );

    private Client _client = null;
    private IOProcessor _processor = null;

    public ClientConnection ( IOProcessor processor )
    {
        _processor = processor;

        _client = new Client ( _processor, getMessageProcessor (), this, false );
        setConnection ( _client.getConnection () );
    }

    public void connect ( SocketAddress remote )
    {
        _client.connect ( remote );
    }

    public void disconnect ()
    {
        if ( _client.getConnection () != null )
        {
            _client.getConnection ().close ();
        }
        else
        {
            _log.warn ( "Client has no connection!" );
        }
    }

    @Override
    public void opened ()
    {
        setConnection ( _client.getConnection () );
        super.opened ();
    }

}
