/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.net.base;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.handlers.PingHandler;
import org.openscada.net.base.handlers.PongHandler;
import org.openscada.net.mina.Messenger;
import org.openscada.net.utils.MessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingService
{
    private final static Logger logger = LoggerFactory.getLogger ( PingService.class );

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
