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

package org.openscada.ae.storage.net;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.openscada.ae.storage.Storage;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.server.net.AbstractServerConnectionHandler;

public class ServerConnectionHandler extends AbstractServerConnectionHandler
{

    public final static String VERSION = "0.1.0";

    private static Logger logger = Logger.getLogger ( ServerConnectionHandler.class );

    private final NetConnectionServerImpl serverImpl;

    public ServerConnectionHandler ( final Storage storage, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.serverImpl = new NetConnectionServerImpl ( this.messenger, storage, this );
    }

    protected void disposeSession ()
    {
        logger.info ( "Disposed session" );
        this.messenger.disconnected ();
    }

    @Override
    protected void cleanUp ()
    {
        logger.info ( "Clean up connection" );

        this.serverImpl.dispose ();

        disposeSession ();
        super.cleanUp ();
    }
}
