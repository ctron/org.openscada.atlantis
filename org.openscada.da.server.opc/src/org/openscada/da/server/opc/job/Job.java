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
 * A base job implementation
 * <p>
 * Note that each instance job might only be executed once!
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public abstract class Job
{
    protected long timeout;

    protected boolean canceled = false;

    protected Throwable error = null;

    protected abstract void run () throws Exception;

    protected abstract void interrupt ();

    public Job ( final long timeout )
    {
        this.timeout = timeout;
    }

    public long getTimeout ()
    {
        return this.timeout;
    }

    public boolean isCanceled ()
    {
        return this.canceled;
    }

    public Throwable getError ()
    {
        return this.error;
    }
}
