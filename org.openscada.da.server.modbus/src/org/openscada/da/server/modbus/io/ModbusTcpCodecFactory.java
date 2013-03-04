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

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.openscada.da.server.modbus.ModbusConstants;
import org.openscada.da.server.modbus.io.message.RequestWrapper;
import org.openscada.da.server.modbus.io.message.request.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusTcpCodecFactory implements ProtocolCodecFactory, ResetableCodecFactory
{
    public static class MbapHeader
    {
        final int transactionIdentifier;

        final int protocolIdentifier;

        final int length;

        final byte unitIdentifier;

        public MbapHeader ( final int transactionIdentifier, final int protocolIdentifier, final int length, final byte unitIdentifier )
        {
            if ( protocolIdentifier != PROTOCOL_IDENTIFIER )
            {
                throw new ModbusProtocolError ( "protocolIdentifier must be " + PROTOCOL_IDENTIFIER + " but is " + protocolIdentifier );
            }
            if ( length > ModbusConstants.MAX_PDU_SIZE + 1 )
            {
                throw new ModbusProtocolError ( "length must be less or equal than " + ( ModbusConstants.MAX_PDU_SIZE + 1 ) );
            }
            this.transactionIdentifier = transactionIdentifier;
            this.protocolIdentifier = protocolIdentifier;
            this.length = length;
            this.unitIdentifier = unitIdentifier;
        }

        public int getTransactionIdentifier ()
        {
            return this.transactionIdentifier;
        }

        public int getProtocolIdentifier ()
        {
            return this.protocolIdentifier;
        }

        public int getLength ()
        {
            return this.length;
        }

        public byte getUnitIdentifier ()
        {
            return this.unitIdentifier;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger ( ModbusTcpCodecFactory.class );

    public static final int MBAP_HEADER_SIZE = 7;

    public static final int PROTOCOL_IDENTIFIER = 0;

    public static final String SESSION_KEY_CURRENT_REQUESTS = ModbusTcpCodecFactory.class.getName () + ".currentRequests";

    private final ProtocolDecoder decoder;

    private final ProtocolEncoder encoder;

    public ModbusTcpCodecFactory ()
    {
        this.decoder = new ModbusTcpDecoder ();
        this.encoder = new ModbusTcpEncoder ();
    }

    @Override
    public ProtocolDecoder getDecoder ( final IoSession session ) throws Exception
    {
        return this.decoder;
    }

    @Override
    public ProtocolEncoder getEncoder ( final IoSession session ) throws Exception
    {
        return this.encoder;
    }

    @SuppressWarnings ( "unchecked" )
    public static RequestMessage getOriginalRequest ( final IoSession session, final int transactionIdentifier ) throws ModbusProtocolError
    {
        Map<Integer, RequestWrapper> requests = (Map<Integer, RequestWrapper>)session.getAttribute ( ModbusTcpCodecFactory.SESSION_KEY_CURRENT_REQUESTS );
        if ( requests == null )
        {
            requests = new HashMap<Integer, RequestWrapper> ();
            session.setAttribute ( ModbusTcpCodecFactory.SESSION_KEY_CURRENT_REQUESTS, requests );
        }
        final RequestWrapper requestWrapper = requests.get ( transactionIdentifier );
        if ( requestWrapper == null )
        {
            throw new ModbusProtocolError ( "no request message for transaction no " + transactionIdentifier + " found" );
        }
        logger.trace ( "getOriginalRequest () = {}", requestWrapper );
        return requestWrapper.getMessage ();
    }

    @Override
    public void reset ( final IoSession session )
    {
        logger.debug ( "reset ()" );
        if ( session != null )
        {
            session.removeAttribute ( ModbusTcpCodecFactory.SESSION_KEY_CURRENT_REQUESTS );
        }
    }
}
