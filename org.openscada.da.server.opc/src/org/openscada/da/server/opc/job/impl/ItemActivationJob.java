/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.job.impl;

import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job activates an opc item in an already exisiting group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class ItemActivationJob extends ThreadJob implements JobResult<ResultSet<Integer>>
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemActivationJob.class );

    public static final long DEFAULT_TIMEOUT = 5000L;

    private final OPCModel model;

    private final Integer[] clientHandles;

    private final boolean state;

    private ResultSet<Integer> result;

    public ItemActivationJob ( final long timeout, final OPCModel model, final boolean state, final Integer[] clientHandles )
    {
        super ( timeout );
        this.model = model;
        this.clientHandles = clientHandles;
        this.state = state;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.debug ( "Item activation" );
        this.result = this.model.getItemMgt ().setActiveState ( this.state, this.clientHandles );
    }

    @Override
    public ResultSet<Integer> getResult ()
    {
        return this.result;
    }
}
