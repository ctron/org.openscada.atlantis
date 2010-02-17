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

package org.openscada.core.net;

import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;
import org.openscada.net.utils.MessageCreator;

public class OperationController
{
    private Messenger messenger = null;

    public OperationController ( final Messenger messenger )
    {
        super ();
        this.messenger = messenger;
    }

    protected void sendACK ( final Message request, final long id )
    {
        final Message message = MessageCreator.createACK ( request );
        message.getValues ().put ( "id", new LongValue ( id ) );
        this.messenger.sendMessage ( message );
    }

    protected void sendFailure ( final Message request, final Throwable e )
    {
        final Message message = MessageCreator.createFailedMessage ( request, e );
        this.messenger.sendMessage ( message );
    }
}
