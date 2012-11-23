/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.server.ngp;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerConnection
{

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnection.class );

    private final IoSession session;

    public ServerConnection ( final IoSession session )
    {
        logger.info ( "Creating new server connection: {}", session );

        this.session = session;
    }

    public void dispose ()
    {
        logger.info ( "Disposing server connection: {}", this.session );
        requestClose ( true );
    }

    protected void sendMessage ( final Object message )
    {
        logger.trace ( "Sending message: {}", message );
        this.session.write ( message );
    }

    public void requestClose ( final boolean immediately )
    {
        this.session.close ( immediately );
    }

    public abstract void messageReceived ( Object message ) throws Exception;
}
