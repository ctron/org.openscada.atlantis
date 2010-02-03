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

/**
 * 
 */
package org.openscada.da.server.exec.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.openscada.da.server.exec.command.ExecutionResult;
import org.openscada.da.server.exec.command.ProcessListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor
{
    private final static Logger logger = LoggerFactory.getLogger ( CommandExecutor.class );

    /**
     * This method executes the specified command on the shell using the passed objects as information provider.
     * 
     */
    public static ExecutionResult executeCommand ( final ProcessBuilder processBuilder, final ProcessListener listener )
    {
        final ExecutionResult result = new ExecutionResult ();

        Process p = null;
        // Execute the command
        try
        {
            // Execute and wait
            final long start = System.currentTimeMillis ();
            p = processBuilder.start ();

            if ( listener != null )
            {
                listener.processCreated ( p );
            }

            p.waitFor ();
            final long end = System.currentTimeMillis ();

            result.setRuntime ( end - start );

            // Get exit value
            final int exitValue = p.exitValue ();
            result.setExitValue ( exitValue );

            // Get result
            final InputStream input = p.getInputStream ();
            result.setOutput ( inputStreamToString ( input ) );

            final InputStream error = p.getErrorStream ();
            result.setErrorOutput ( inputStreamToString ( error ) );
        }
        catch ( final Throwable e )
        {
            result.setExecutionError ( e );
            return result;
        }
        finally
        {
            if ( p != null )
            {
                closeStream ( p.getErrorStream () );
                closeStream ( p.getInputStream () );
                closeStream ( p.getOutputStream () );
            }

            if ( listener != null )
            {
                listener.processCompleted ();
            }
        }

        return result;
    }

    protected static void closeStream ( final Object stream )
    {
        if ( stream == null )
        {
            return;
        }
        try
        {
            if ( stream instanceof InputStream )
            {
                ( (InputStream)stream ).close ();
            }
            else if ( stream instanceof OutputStream )
            {
                ( (OutputStream)stream ).close ();
            }
        }
        catch ( final IOException e )
        {
            logger.warn ( "Failed to close stream", e );
        }
    }

    /**
     * Read from an inputStream and place the output in a string
     * @param inputStream
     * @return
     */
    private static String inputStreamToString ( final InputStream inputStream ) throws IOException
    {
        final InputStreamReader inputStreamReader = new InputStreamReader ( inputStream );
        final BufferedReader br = new BufferedReader ( inputStreamReader );

        String output = "";
        String line = "";
        while ( ( line = br.readLine () ) != null )
        {
            output += line;
        }
        return output;
    }
}
