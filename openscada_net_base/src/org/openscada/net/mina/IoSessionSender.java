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

import org.apache.mina.core.session.IoSession;
import org.openscada.net.base.data.Message;

public class IoSessionSender implements MessageSender
{
    private static final long MAX_SEQUENCE = 0x7FFFFFFF;

    private static final long INIT_SEQUENCE = 1;

    private long sequence = INIT_SEQUENCE;

    private final IoSession session;

    public IoSessionSender ( final IoSession session )
    {
        this.session = session;
    }

    public boolean sendMessage ( final Message message, final PrepareSendHandler handler )
    {
        synchronized ( this )
        {
            message.setSequence ( nextSequence () );

            // if we have a prepare send handler .. notify
            if ( handler != null )
            {
                handler.prepareSend ( message );
            }

            this.session.write ( message );
        }

        return true;
    }

    private long nextSequence ()
    {
        final long seq = this.sequence++;
        if ( this.sequence >= MAX_SEQUENCE )
        {
            this.sequence = INIT_SEQUENCE;
        }
        return seq;
    }

    public void close ()
    {
        this.session.close ( true );
    }

    @Override
    public String toString ()
    {
        return this.session.toString ();
    }

}
