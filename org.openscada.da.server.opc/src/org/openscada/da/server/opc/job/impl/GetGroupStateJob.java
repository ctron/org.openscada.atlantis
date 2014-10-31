/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import org.openscada.opc.dcom.da.OPCGroupState;

/**
 * Attach to the connection point of the group
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
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
