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

package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.net.Client;

public class AutoReconnectClientConnection extends ConnectionHandlerBase
{
    @SuppressWarnings ( "unused" )
    private static Logger log = Logger.getLogger ( AutoReconnectClientConnection.class );

    private Client client = null;

    private IOProcessor processor = null;

    private SocketAddress remote = null;

    public AutoReconnectClientConnection ( final IOProcessor processor, final SocketAddress remote )
    {
        super ( processor.getScheduler () );
        this.processor = processor;
        this.remote = remote;
    }

    /**
     * start connecting to the server
     */
    public void start ()
    {
        this.client = new Client ( this.processor, getMessageProcessor (), this, true );
        setConnection ( this.client.getConnection () );
        this.client.connect ( this.remote );
    }

    @Override
    public void opened ()
    {
        setConnection ( this.client.getConnection () );
        super.opened ();
    }
}
