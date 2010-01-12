/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.job.impl;

import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.da.OPCGroupState;

/**
 * Attach to the connection point of the group
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class GetGroupStateJob extends ThreadJob implements JobResult<OPCGroupState>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final OPCModel model;

    private OPCGroupState result;

    public GetGroupStateJob ( final long timeout, final OPCModel model )
    {
        super ( timeout );
        this.model = model;
    }

    @Override
    protected void perform () throws Exception
    {
        this.result = this.model.getGroup ().getState ();
    }

    public OPCGroupState getResult ()
    {
        return this.result;
    }
}
