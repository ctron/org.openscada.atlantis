/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.job;

/**
 * A work unit which consists of a job and a handler that receives the jobs results.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
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
