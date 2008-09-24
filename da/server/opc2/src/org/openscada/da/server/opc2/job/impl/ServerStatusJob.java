/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.job.impl;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.JobResult;
import org.openscada.da.server.opc2.job.ThreadJob;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;

/**
 * This job queries the server status
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class ServerStatusJob extends ThreadJob implements JobResult<OPCSERVERSTATUS>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( ServerStatusJob.class );

    private OPCModel model;

    private OPCSERVERSTATUS status;

    public ServerStatusJob ( long timeout, OPCModel model )
    {
        super ( timeout );
        this.model = model;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request server status" );
        this.status = model.getServer ().getStatus ();
    }

    public OPCSERVERSTATUS getStatus ()
    {
        return status;
    }

    public OPCSERVERSTATUS getResult ()
    {
        return getStatus ();
    }

}
