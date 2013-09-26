/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.exec.command;

public class ExecutionResult
{
    /**
     * The exit value of the command call, <code>null</code> if the application is still running
     */
    private Integer exitValue = null;

    /**
     * The output from the process
     */
    private String output = "";

    /**
     * The error output from the process
     */
    private String errorOutput = "";

    /**
     * An exception that was caused by the execution 
     */
    private Throwable executionError = null;

    /**
     * The time the application took for completing, <code>null</code> if the process is still running
     */
    private Long runtime;

    public Integer getExitValue ()
    {
        return this.exitValue;
    }

    public void setExitValue ( final Integer exitValue )
    {
        this.exitValue = exitValue;
    }

    public String getOutput ()
    {
        return this.output;
    }

    public void setOutput ( final String output )
    {
        this.output = output;
    }

    public String getErrorOutput ()
    {
        return this.errorOutput;
    }

    public void setErrorOutput ( final String errorOutput )
    {
        this.errorOutput = errorOutput;
    }

    public Throwable getExecutionError ()
    {
        return this.executionError;
    }

    public void setExecutionError ( final Throwable executionError )
    {
        this.executionError = executionError;
    }

    public Long getRuntime ()
    {
        return this.runtime;
    }

    public void setRuntime ( final Long runtime )
    {
        this.runtime = runtime;
    }

    @Override
    public String toString ()
    {
        return this.output;
    }
}
