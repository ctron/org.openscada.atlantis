/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.core.client.ngp;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientConnectionHandler implements IoHandler
{
    private final static Logger logger = LoggerFactory.getLogger ( ClientConnectionHandler.class );

    protected final ClientBaseConnection connection;

    public ClientConnectionHandler ( final ClientBaseConnection connection )
    {
        this.connection = connection;
    }

    @Override
    public void exceptionCaught ( final IoSession session, final Throwable error ) throws Exception
    {
        logger.info ( "exceptionCaught - session: " + session, error );

        this.connection.performClosed ( session, error );
    }

    @Override
    public void messageReceived ( final IoSession session, final Object message ) throws Exception
    {
        this.connection.messageReceived ( session, message );
    }

    @Override
    public void messageSent ( final IoSession session, final Object message ) throws Exception
    {
    }

    @Override
    public void sessionClosed ( final IoSession session ) throws Exception
    {
        this.connection.performClosed ( session, null );
    }

    @Override
    public void sessionIdle ( final IoSession session, final IdleStatus idleState ) throws Exception
    {
    }

    @Override
    public void sessionOpened ( final IoSession session ) throws Exception
    {
        this.connection.performOpened ( session );
    }

}