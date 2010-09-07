/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
