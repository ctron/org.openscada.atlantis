/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
