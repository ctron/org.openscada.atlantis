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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.eclipse.scada.da.server.modbus.io.ModbusTcpCodecFactory.MbapHeader;
import org.eclipse.scada.da.server.modbus.io.message.ResponseWrapper;
import org.eclipse.scada.da.server.modbus.io.message.request.RequestMessage;
import org.eclipse.scada.da.server.modbus.io.message.response.ResponseMessage;

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
