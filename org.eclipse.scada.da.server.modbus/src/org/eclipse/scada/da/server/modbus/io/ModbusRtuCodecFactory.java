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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.eclipse.scada.da.server.modbus.io.message.RequestWrapper;
import org.eclipse.scada.da.server.modbus.io.message.request.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusRtuCodecFactory implements ProtocolCodecFactory, ResetableCodecFactory
{
    private static final Logger logger = LoggerFactory.getLogger ( ModbusRtuCodecFactory.class );

    public static final String SESSION_KEY_CURRENT_REQUEST = ModbusRtuCodecFactory.class.getName () + ".currentRequest";

    public static final int RTU_HEADER_SIZE = 3;

    private final ProtocolDecoder decoder;

    private final ProtocolEncoder encoder;

    public ModbusRtuCodecFactory ( final ScheduledExecutorService scheduler, final long timeout, final TimeUnit timeUnit )
    {
        this.decoder = new ModbusRtuDecoder ( scheduler, timeout, timeUnit );
        this.encoder = new ModbusRtuEncoder ();
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

    @Override
    public void reset ( final IoSession session )
    {
        logger.debug ( "reset ()" );
        if ( session != null )
        {
            session.removeAttribute ( SESSION_KEY_CURRENT_REQUEST );
        }
    }

    public static RequestMessage getOriginalRequest ( final IoSession session ) throws ModbusProtocolError
    {
        final RequestWrapper requestWrapper = (RequestWrapper)session.getAttribute ( SESSION_KEY_CURRENT_REQUEST );
        if ( requestWrapper == null )
        {
            throw new ModbusProtocolError ( "no request message found" );
        }
        return requestWrapper.getMessage ();
    }
}
