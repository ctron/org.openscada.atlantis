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
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.opc.dcom.da.impl.OPCSyncIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This method performs a sync write operation
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class SyncWriteJob extends ThreadJob implements JobResult<ResultSet<WriteRequest>>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( SyncWriteJob.class );

    private final OPCModel model;

    private final WriteRequest[] writeRequests;

    private ResultSet<WriteRequest> result;

    public SyncWriteJob ( final long timeout, final OPCModel model, final WriteRequest[] writeRequests )
    {
        super ( timeout );
        this.model = model;
        this.writeRequests = writeRequests;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.debug ( "Perform sync write" );

        final OPCSyncIO syncIo = this.model.getSyncIo ();
        if ( syncIo != null )
        {
            this.result = syncIo.write ( this.writeRequests );
        }
    }

    @Override
    public ResultSet<WriteRequest> getResult ()
    {
        return this.result;
    }
}
