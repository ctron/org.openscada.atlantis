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

package org.openscada.da.server.dave;

import org.openscada.da.server.common.memory.AbstractRequestBlock;
import org.openscada.protocols.dave.DaveReadRequest;
import org.openscada.protocols.dave.DaveReadRequest.Request;
import org.openscada.protocols.dave.DaveReadResult;
import org.openscada.protocols.dave.DaveReadResult.Result;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveRequestBlock extends AbstractRequestBlock
{
    private final static Logger logger = LoggerFactory.getLogger ( DaveRequestBlock.class );

    private final Request request;

    private final DaveDevice device;

    public DaveRequestBlock ( final String id, final String name, final String mainTypeName, final DaveDevice device, final BundleContext context, final Request request, final boolean enableStatistics, final long period )
    {
        super ( context, device.getExecutor (), mainTypeName, device.getVarItemId ( name ), device.getItemId ( id ), enableStatistics, period, request.getCount (), 0L );

        this.device = device;
        this.request = request;
    }

    @Override
    public long getPollRequestTimeout ()
    {
        return 0;
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

    /**
     * Handle a response from the device
     * 
     * @param response
     *            the response to handle
     */
    public synchronized void handleResponse ( final Result response )
    {
        if ( response.isError () )
        {
            handleError ( response.getError () );
        }
        else
        {
            handleData ( response.getData () );
        }
    }

    @Override
    public void handleTimeout ()
    {
        // this is a no-op since we don't have poll timeouts
    }

    @Override
    public String toString ()
    {
        return String.format ( "[Request - %s]", this.request );
    }

    @Override
    public boolean handleMessage ( final Object message )
    {
        if ( message instanceof DaveReadResult )
        {
            // we should have exactly one reply
            for ( final Result result : ( (DaveReadResult)message ).getResult () )
            {
                handleResponse ( result );
                return true;
            }
        }
        else
        {
            logger.warn ( "Got wrong message as reply: {}", message );
        }
        return false;
    }

    @Override
    public Object createPollRequest ()
    {
        final DaveReadRequest request = new DaveReadRequest ();
        request.addRequest ( this.request );
        return request;
    }

    @Override
    public int getStartAddress ()
    {
        return this.request.getStart ();
    }

    private int toGlobalAddress ( final int address )
    {
        return address + this.request.getStart ();
    }

    @Override
    public void writeBit ( final int address, final int subIndex, final boolean value )
    {
        this.device.writeBit ( this, toGlobalAddress ( address ), subIndex, value );
    }

    @Override
    public void writeData ( final int blockAddress, final byte[] data )
    {
        this.device.writeData ( this, toGlobalAddress ( blockAddress ), data );
    }
}
