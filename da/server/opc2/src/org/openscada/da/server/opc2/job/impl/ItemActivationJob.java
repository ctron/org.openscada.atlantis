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

/**
 * This job activates an opc item in an already exisiting group
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class ItemActivationJob extends ThreadJob implements JobResult<ResultSet<Integer>>
{
    private static Logger log = Logger.getLogger ( ItemActivationJob.class );

    private OPCModel model;

    private Integer[] clientHandles;

    private boolean state;

    private ResultSet<Integer> result;

    public ItemActivationJob ( OPCModel model, boolean state, Integer[] clientHandles )
    {
        super ( 5000 );
        this.model = model;
        this.clientHandles = clientHandles;
        this.state = state;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request server status" );
        this.result = this.model.getItemMgt ().setActiveState ( state, clientHandles );
    }

    public ResultSet<Integer> getResult ()
    {
        return result;
    }
}
