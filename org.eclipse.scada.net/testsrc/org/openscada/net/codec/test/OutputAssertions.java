/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.net.codec.test;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class OutputAssertions implements ProtocolEncoderOutput
{

    private final IoBuffer buffer = IoBuffer.allocate ( 0 );

    public WriteFuture flush ()
    {
        return null;
    }

    public void mergeAll ()
    {
    }

    public void write ( final Object encodedMessage )
    {
        if ( encodedMessage instanceof IoBuffer )
        {
            this.buffer.setAutoExpand ( true );
            this.buffer.put ( (IoBuffer)encodedMessage );
        }
    }

    public IoBuffer getBuffer ()
    {
        return this.buffer;
    }
}
