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
 * Handle the result of a job execution
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public interface JobHandler
{
    /**
     * Handle the case when the job was finished successfully
     */
    public abstract void handleSuccess ();

    /**
     * Handle the case when the job was started, but then interrupted from the work queue 
     */
    public abstract void handleInterrupted ();

    /**
     * Handle the case the job completed but failed
     * @param e the error that occurred
     */
    public abstract void handleFailure ( Throwable e );
}
