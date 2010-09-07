/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.dave.data;

import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.openscada.protocols.dave.DaveReadRequest.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractAttribute
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractAttribute.class );

    protected final String name;

    protected int offset;

    protected DaveDevice device;

    protected DaveRequestBlock block;

    private boolean stopped;

    public AbstractAttribute ( final String name )
    {
        super ();
        this.name = name;
        this.stopped = true;
    }

    public String getName ()
    {
        return this.name;
    }

    public void start ( final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        logger.debug ( "Starting attribute: {}", this.name );
        this.stopped = false;

        assert device != null;
        assert block != null;

        this.device = device;
        this.block = block;
        this.offset = offset;
    }

    public void stop ()
    {
        logger.debug ( "Stopping attribute: {}", this.name );

        this.stopped = true;

        this.device = null;
        this.block = null;
    }

    protected int toAddress ( final int localAddress )
    {
        if ( this.stopped )
        {
            logger.error ( "isStopped" );
        }

        final Request request = this.block.getRequest ();

        if ( request == null )
        {
            logger.error ( "no request found for block: {}", this.block );
        }

        return localAddress + this.offset - request.getStart ();
    }

}