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
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job queries the server status
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class ServerStatusJob extends ThreadJob implements JobResult<OPCSERVERSTATUS>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( ServerStatusJob.class );

    private final OPCModel model;

    private OPCSERVERSTATUS status;

    public ServerStatusJob ( final long timeout, final OPCModel model )
    {
        super ( timeout );
        this.model = model;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.debug ( "Request server status" );
        this.status = this.model.getServer ().getStatus ();
    }

    public OPCSERVERSTATUS getStatus ()
    {
        return this.status;
    }

    @Override
    public OPCSERVERSTATUS getResult ()
    {
        return getStatus ();
    }

}
