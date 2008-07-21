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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class CommandExecutor
{
    private static Logger logger = Logger.getLogger ( CommandExecutor.class );
    
    /**
     * This method executes the specified command on the shell using the passed objects as information provider.
     * @param cmd command string including arguments
     * @param formatArguments objects that will be parsed into the command string
     */
    public static CommandResult executeCommand ( String cmd )
    {
        // Prepare a result
        CommandResult result = new CommandResult ();
        result.setError ( true );
        result.setMessage ( "OK" );

        Process p = null;
        // Execute the command
        try
        {
            // Execute and wait
            p = Runtime.getRuntime ().exec ( cmd );
            p.waitFor ();

            // Get exit value
            int exitValue = p.exitValue ();
            result.setExitValue ( exitValue );

            // Get result
            InputStream input = p.getInputStream ();
            result.setOutput ( inputStreamToString ( input ) );

            InputStream error = p.getErrorStream ();
            result.setErrorOutput ( inputStreamToString ( error ) );
        }
        catch ( Exception e )
        {
            result.setMessage ( String.format ( "Unable to execute command! Detailed message: %1$s", e.getMessage () ) );
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
        }

        result.setError ( false );
        return result;
    }

    protected static void closeStream ( Object stream )
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
        catch ( IOException e )
        {
            logger.warn ( "Failed to close stream", e );
        }
    }

    /**
     * Read from an inputStream and place the output in a string
     * @param inputStream
     * @return
     */
    private static String inputStreamToString ( InputStream inputStream ) throws IOException
    {
        InputStreamReader inputStreamReader = new InputStreamReader ( inputStream );
        BufferedReader br = new BufferedReader ( inputStreamReader );

        String output = "";
        String line = "";
        while ( ( line = br.readLine () ) != null )
        {
            output += line;
        }
        return output;
    }
}
