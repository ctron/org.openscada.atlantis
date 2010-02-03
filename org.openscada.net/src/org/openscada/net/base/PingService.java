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

package org.openscada.net.base;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.handlers.PingHandler;
import org.openscada.net.base.handlers.PongHandler;
import org.openscada.net.mina.Messenger;
import org.openscada.net.utils.MessageCreator;

public class PingService
{

    private static Logger logger = Logger.getLogger ( PingService.class );

    private final Messenger messenger;

    public PingService ( final Messenger messenger )
    {
        this.messenger = messenger;

        this.messenger.setHandler ( Message.CC_PING, new PingHandler ( this.messenger ) );
        this.messenger.setHandler ( Message.CC_PONG, new PongHandler () );
    }

    public void sendPing ()
    {
        logger.debug ( "Sending ping" );
        this.messenger.sendMessage ( MessageCreator.createPing () );
    }

}
