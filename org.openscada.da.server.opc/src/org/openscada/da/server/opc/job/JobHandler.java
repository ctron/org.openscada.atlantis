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
 * Handle the result of a job execution
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
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
