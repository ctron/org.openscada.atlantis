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
import org.openscada.opc.dcom.common.EventHandler;
import org.openscada.opc.dcom.da.IOPCDataCallback;
import org.openscada.opc.dcom.da.impl.OPCGroupStateMgt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Attach to the connection point of the group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class AttachGroupJob extends ThreadJob implements JobResult<EventHandler>
{
    public static final long DEFAULT_TIMEOUT = 5000L;

    private final static Logger logger = LoggerFactory.getLogger ( AttachGroupJob.class );

    private final OPCModel model;

    private final IOPCDataCallback dataCallback;

    private EventHandler result;

    public AttachGroupJob ( final long timeout, final OPCModel model, final IOPCDataCallback dataCallback )
    {
        super ( timeout );
        this.model = model;
        this.dataCallback = dataCallback;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.info ( "Perform group attach" );

        final OPCGroupStateMgt group = this.model.getGroup ();
        if ( group != null )
        {
            this.result = group.attach ( this.dataCallback );
        }
    }

    @Override
    public EventHandler getResult ()
    {
        return this.result;
    }

}
