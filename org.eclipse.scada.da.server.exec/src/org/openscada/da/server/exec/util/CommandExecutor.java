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

package org.openscada.da.server.exec.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

            closeStream ( p.getOutputStream () );

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
            if ( listener != null )
            {
                listener.processCompleted ();
            }

            if ( p != null )
            {
                closeStream ( p.getErrorStream () );
                closeStream ( p.getInputStream () );
                closeStream ( p.getOutputStream () );
            }
        }

        return result;
    }

    protected static void closeStream ( final Closeable stream )
    {
        if ( stream == null )
        {
            return;
        }
        try
        {
            stream.close ();
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
