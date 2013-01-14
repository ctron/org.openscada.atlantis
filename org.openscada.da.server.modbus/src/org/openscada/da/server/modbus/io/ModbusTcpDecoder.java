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

package org.openscada.da.server.modbus.io;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.openscada.da.server.modbus.io.ModbusTcpCodecFactory.MbapHeader;
import org.openscada.da.server.modbus.io.message.ResponseWrapper;
import org.openscada.da.server.modbus.io.message.request.RequestMessage;
import org.openscada.da.server.modbus.io.message.response.ResponseMessage;

public class ModbusTcpDecoder extends CumulativeProtocolDecoder
{
    private MbapHeader header = null;

    @Override
    protected boolean doDecode ( final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out ) throws Exception
    {
        try
        {
            if ( this.header == null )
            {
                if ( in.remaining () < ModbusTcpCodecFactory.MBAP_HEADER_SIZE )
                {
                    return false;
                }
                this.header = readHeader ( in );
            }
            if ( in.remaining () == this.header.getLength () - 1 )
            {
                final IoBuffer pdu = IoBuffer.allocate ( this.header.getLength () - 1 );
                for ( int i = 0; i < this.header.getLength () - 1; i++ )
                {
                    pdu.put ( in.get () );
                }
                pdu.flip ();
                final RequestMessage originalRequest = ModbusTcpCodecFactory.getOriginalRequest ( session, this.header.getTransactionIdentifier () );
                final ResponseMessage responseMessage = ModbusHelper.decodeResponse ( originalRequest, pdu );
                out.write ( new ResponseWrapper ( this.header.getUnitIdentifier (), responseMessage, originalRequest ) );
                this.header = null;
                return true;
            }
            else if ( in.remaining () > this.header.getLength () - 1 )
            {
                throw new ModbusProtocolError ( "received data is more (" + in.remaining () + ") than expected length (" + ( this.header.getLength () - 1 ) + ")" );
            }
            return false;
        }
        catch ( final Throwable e )
        {
            this.header = null;
            throw new Exception ( e );
        }
    }

    private MbapHeader readHeader ( final IoBuffer in )
    {
        final int transactionIdentifier = in.getShort ();
        final int protocolIdentifier = in.getShort ();
        final int length = in.getShort ();
        final byte unitIdentifier = in.get ();
        return new MbapHeader ( transactionIdentifier, protocolIdentifier, length, unitIdentifier );
    }
}
