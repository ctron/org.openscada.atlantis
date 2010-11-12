/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.apache.log4j.Logger;
import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMSTATE;

/**
 * This job performs a sync read operation
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class SyncReadJob extends ThreadJob implements JobResult<KeyedResultSet<Integer, OPCITEMSTATE>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private static Logger log = Logger.getLogger ( SyncReadJob.class );

    private final OPCModel model;

    private final Integer[] clientHandles;

    private final OPCDATASOURCE dataSource;

    private KeyedResultSet<Integer, OPCITEMSTATE> result;

    public SyncReadJob ( final long timeout, final OPCModel model, final OPCDATASOURCE dataSource, final Integer[] clientHandles )
    {
        super ( timeout );
        this.model = model;
        this.clientHandles = clientHandles;
        this.dataSource = dataSource;
    }

    @Override
    protected void perform () throws Exception
    {
        log.debug ( "Request server status" );
        this.result = this.model.getSyncIo ().read ( this.dataSource, this.clientHandles );
    }

    public KeyedResultSet<Integer, OPCITEMSTATE> getResult ()
    {
        return this.result;
    }
}
