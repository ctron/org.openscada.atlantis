/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.modbus.io;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.eclipse.scada.da.server.modbus.io.message.RequestWrapper;

public class ModbusRtuEncoder implements ProtocolEncoder
{

    @Override
    public void dispose ( final IoSession session ) throws Exception
    {
    }

    @Override
    public void encode ( final IoSession session, final Object message, final ProtocolEncoderOutput out ) throws Exception
    {
        if ( ! ( message instanceof RequestWrapper ) )
        {
            throw new ModbusProtocolError ( "message is not of type RequestWrapper" );
        }
        final RequestWrapper requestWrapper = (RequestWrapper)message;

        session.setAttribute ( ModbusRtuCodecFactory.SESSION_KEY_CURRENT_REQUEST, requestWrapper );

        final IoBuffer pdu = ModbusHelper.encodeRequest ( requestWrapper.getMessage () );
        final IoBuffer buffer = IoBuffer.allocate ( ModbusRtuCodecFactory.RTU_HEADER_SIZE + pdu.limit () );
        buffer.put ( requestWrapper.getUnitIdentifier () );
        buffer.put ( pdu );
        final int crc = CRC.crc16 ( buffer.array (), 0, pdu.limit () + 1 );
        buffer.order ( ByteOrder.LITTLE_ENDIAN );
        buffer.putShort ( (short)crc );
        buffer.order ( ByteOrder.BIG_ENDIAN );
        buffer.flip ();
        out.write ( buffer );
    }

}
