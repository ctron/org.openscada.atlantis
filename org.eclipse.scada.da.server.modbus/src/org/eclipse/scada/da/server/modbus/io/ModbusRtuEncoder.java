/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
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
