/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exec.command;

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
