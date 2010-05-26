/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.net.base.handlers;

import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;
import org.openscada.net.utils.MessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingHandler implements MessageListener
{

    private final static Logger logger = LoggerFactory.getLogger ( PingHandler.class );

    private final Messenger messenger;

    public PingHandler ( final Messenger messenger )
    {
        this.messenger = messenger;
    }

    public void messageReceived ( final Message message )
    {
        logger.debug ( "Ping request: {}", message.getValues ().get ( "ping-data" ) );

        this.messenger.sendMessage ( MessageCreator.createPong ( message ) );
    }

}
