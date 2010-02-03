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

package org.openscada.net.base.handlers;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;

public class PongHandler implements MessageListener
{
    private static Logger logger = Logger.getLogger ( PongHandler.class );

    public void messageReceived ( final Message message )
    {
        // no-op
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( "Pong request: " + message.getValues ().get ( "pong-data" ) );
        }
    }

}
