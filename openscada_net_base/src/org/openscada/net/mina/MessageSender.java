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

package org.openscada.net.mina;

import org.openscada.net.base.data.Message;

public interface MessageSender
{

    /**
     * Send a message out to somewhere
     * @param message the message to send
     * @param prepareSendHandler the prepare handler to call after the message is prepared
     * @return <code>true</code> if the message was send out (does not guarantee a successful delivery!)
     */
    public boolean sendMessage ( Message message, PrepareSendHandler prepareSendHandler );

    /**
     * Close the session of the sender
     */
    public void close ();
}
