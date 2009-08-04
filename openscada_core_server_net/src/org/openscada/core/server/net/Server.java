/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.core.server.net;

import java.io.IOException;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.net.ConnectionHelper;
import org.openscada.net.mina.SocketImpl;

public class Server
{
    private IoAcceptor acceptor;

    private final ConnectionInformation connectionInformation;

    public Server ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    public void start ( final SingleSessionIoHandlerDelegate ioHandler ) throws IOException
    {
        this.acceptor = createAcceptor ( ioHandler );
    }

    private IoAcceptor createAcceptor ( final IoHandler ioHandler ) throws IOException
    {
        IoAcceptor acceptor = null;

        final SocketImpl socketImpl = SocketImpl.fromName ( this.connectionInformation.getProperties ().get ( "socketImpl" ) );

        // create the acceptor
        acceptor = socketImpl.createAcceptor ();

        // set up the filter chain
        ConnectionHelper.setupFilterChain ( this.connectionInformation, acceptor.getFilterChain (), false );

        // set the session handler
        acceptor.setHandler ( ioHandler );

        // check if the primary target is the wildcard target
        String host = this.connectionInformation.getTarget ();
        if ( host != null && ( host.length () == 0 || "*".equals ( host ) ) )
        {
            host = null;
        }

        // bind
        acceptor.bind ( socketImpl.doLookup ( host, this.connectionInformation.getSecondaryTarget () ) );

        return acceptor;
    }

    public void dispose ()
    {
        if ( this.acceptor != null )
        {
            this.acceptor.unbind ();
            this.acceptor.dispose ();
            this.acceptor = null;
        }
    }
}
