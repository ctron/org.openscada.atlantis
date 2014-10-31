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
 * A job result interface which allows for
 * a common method of getting back the result of a job
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 * @param <T> the type of the result
 */
public interface JobResult<T>
{
    /**
     * Get the result from the job
     * @return the job result
     */
    public abstract T getResult ();
}
