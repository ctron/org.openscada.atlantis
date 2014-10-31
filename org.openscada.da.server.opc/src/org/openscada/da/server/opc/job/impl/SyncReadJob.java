/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job.impl;

import org.openscada.da.server.opc.connection.OPCModel;
import org.openscada.da.server.opc.job.JobResult;
import org.openscada.da.server.opc.job.ThreadJob;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job performs a sync read operation
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class SyncReadJob extends ThreadJob implements JobResult<KeyedResultSet<Integer, OPCITEMSTATE>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( SyncReadJob.class );

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
        logger.debug ( "Sync read job" );
        this.result = this.model.getSyncIo ().read ( this.dataSource, this.clientHandles );
    }

    @Override
    public KeyedResultSet<Integer, OPCITEMSTATE> getResult ()
    {
        return this.result;
    }
}
