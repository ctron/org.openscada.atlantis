/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job;

/**
 * A work unit which consists of a job and a handler that receives the jobs results.
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class WorkUnit
{
    private final Job job;

    private final JobHandler jobHandler;

    public WorkUnit ( final Job job, final JobHandler jobHandler )
    {
        this.job = job;
        this.jobHandler = jobHandler;
    }

    public Job getJob ()
    {
        return this.job;
    }

    public JobHandler getJobHandler ()
    {
        return this.jobHandler;
    }
}
