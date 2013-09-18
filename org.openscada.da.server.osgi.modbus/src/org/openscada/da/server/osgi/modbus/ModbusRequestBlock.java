/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 IBH SYSTEMS GmbH (http://ibh-systems.com)
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

package org.openscada.da.server.osgi.modbus;

import java.util.concurrent.Executor;

import org.openscada.da.server.common.memory.AbstractRequestBlock;
import org.openscada.protocol.modbus.message.ErrorResponse;
import org.openscada.protocol.modbus.message.ReadResponse;
import org.openscada.protocol.modbus.message.WriteDataRequest;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ModbusRequestBlock extends AbstractRequestBlock
{
    private final static Logger logger = LoggerFactory.getLogger ( ModbusRequestBlock.class );

    private final Request request;

    private final ModbusSlave slave;

    private final String id;

    public ModbusRequestBlock ( final Executor executor, final String id, final String name, final String mainTypeName, final ModbusSlave slave, final BundleContext context, final Request request, final boolean enableStatistics, final long period )
    {
        super ( context, executor, mainTypeName, "modbus." + id, "modbus." + id, enableStatistics, period, request.getCount (), slave.getTimeoutQuietPeriod () );

        this.id = id;

        this.request = request;
        this.slave = slave;
    }

    @Override
    public long getPollRequestTimeout ()
    {
        return this.request.getTimeout ();
    }

    /**
     * The the configured request
     * 
     * @return the request
     */
    public Request getRequest ()
    {
        return this.request;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[Request - %s]", this.request );
    }

    @Override
    public boolean handleMessage ( final Object message )
    {
        MDC.put ( "modbus.block", this.id );
        try
        {
            logger.debug ( "Handle message - message: {}", message );

            if ( message instanceof ErrorResponse )
            {
                logger.debug ( "Handle error" );
                final byte slaveAddress = ( (ErrorResponse)message ).getUnitIdentifier ();
                if ( this.slave.getSlaveAddress () != slaveAddress )
                {
                    logger.info ( "Reply was not for us" );
                    return false;
                }
                handleError ( ( (ErrorResponse)message ).getExceptionCode () );

                return true;
            }
            else if ( message instanceof ReadResponse )
            {
                logger.debug ( "Handle data" );
                final byte slaveAddress = ( (ReadResponse)message ).getUnitIdentifier ();
                if ( this.slave.getSlaveAddress () != slaveAddress )
                {
                    logger.info ( "Reply was not for us (we: {}, they: {})", this.slave.getSlaveAddress (), slaveAddress );
                    return false;
                }
                handleData ( ( (ReadResponse)message ).getData () );
                return true;
            }
            else
            {
                logger.info ( "Unknown message" );
                return false;
            }
        }
        finally
        {
            MDC.remove ( "modbus.block" );
        }
    }

    @Override
    public Object createPollRequest ()
    {
        return this.slave.createPollRequest ( this.request );
    }

    @Override
    public int getStartAddress ()
    {
        return this.request.getStartAddress ();
    }

    private int toGlobalAddress ( final int blockAddress )
    {
        return this.request.getStartAddress () + blockAddress;
    }

    @Override
    public void writeBit ( final int blockAddress, final int subIndex, final boolean value )
    {
        this.slave.writeCommand ( WriteDataRequest.createWriteCoil ( this.slave.getSlaveAddress (), toGlobalAddress ( blockAddress ), value ) );
    }

    @Override
    public void writeData ( final int blockAddress, final byte[] data )
    {
        this.slave.writeCommand ( new WriteDataRequest ( this.slave.getSlaveAddress (), toGlobalAddress ( blockAddress ), data ) );
    }

}
