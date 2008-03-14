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
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.WriteRequest;

/**
 * This method performs a sync write operation
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class SyncWriteJob extends ThreadJob implements JobResult<ResultSet<WriteRequest>>
{
    private static Logger log = Logger.getLogger ( SyncWriteJob.class );

    private OPCModel model;

    private WriteRequest[] writeRequests;

    private ResultSet<WriteRequest> result;

    public SyncWriteJob ( OPCModel model, WriteRequest[] writeRequests )
    {
        super ( 5000 );
        this.model = model;
        this.writeRequests = writeRequests;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request server status" );
        this.result = this.model.getSyncIo ().write ( this.writeRequests );
    }

    public ResultSet<WriteRequest> getResult ()
    {
        return result;
    }
}
