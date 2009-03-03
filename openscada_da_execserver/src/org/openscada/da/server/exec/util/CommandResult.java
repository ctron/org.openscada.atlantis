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

/**
 * 
 */
package org.openscada.da.server.exec.util;

public class CommandResult
{
    /**
     * In case of error an error message
     */
    private String message = "";

    /**
     * Indicating a global success or failure
     */
    private boolean error = true;

    /**
     * The exit value of the command call
     */
    private int exitValue = -1;

    /**
     * The output from the process
     */
    private String output = "";

    /**
     * The error output from the process
     */
    private String errorOutput = "";

    /**
     * @return the message
     */
    public String getMessage ()
    {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage ( final String message )
    {
        this.message = message;
    }

    /**
     * @return the error
     */
    public boolean isError ()
    {
        return this.error;
    }

    /**
     * @param error the error to set
     */
    public void setError ( final boolean error )
    {
        this.error = error;
    }

    /**
     * @return the exitValue
     */
    public int getExitValue ()
    {
        return this.exitValue;
    }

    /**
     * @param exitValue the exitValue to set
     */
    public void setExitValue ( final int exitValue )
    {
        this.exitValue = exitValue;
    }

    /**
     * @return the output
     */
    public String getOutput ()
    {
        return this.output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput ( final String output )
    {
        this.output = output;
    }

    /**
     * @return the errorOutput
     */
    public String getErrorOutput ()
    {
        return this.errorOutput;
    }

    /**
     * @param errorOutput the errorOutput to set
     */
    public void setErrorOutput ( final String errorOutput )
    {
        this.errorOutput = errorOutput;
    }
}
