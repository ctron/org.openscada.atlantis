/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.utils.str.StringHelper;

public class CommandExecutor
{
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger ( CommandExecutor.class );

    /**
     * This method executes the specified command on the shell using the passed objects as information provider.
     * @param cmd command string including arguments
     * @param formatArguments objects that will be parsed into the command string
     * @throws Exception in case of any error
     */
    public static void executeCommand ( List<String> cmd, Object[] formatArguments ) throws Exception
    {
        String[] cmdArguments = new String[cmd.size ()];
        try
        {
            for ( int i = 0; i < cmdArguments.length; i++ )
            {
                cmdArguments[i] = String.format ( cmd.get ( i ), formatArguments );
            }
        }
        catch ( Exception e )
        {
            String message = String.format ( "Unable to create command line arguments! Detailed message: %1$s", e.getMessage () );
            logger.error ( message, e );
            throw new Exception ( message, e );
        }
        logger.info ( "Executing shell command: " + StringHelper.join ( cmdArguments, " " ) );
        try
        {
            Process p = Runtime.getRuntime ().exec ( cmdArguments );
            p.waitFor ();
            int exitValue = p.exitValue ();
            if ( exitValue != 0 )
            {
                String message = String.format ( "Process did not finish properly. Error code: %1$s", +exitValue );
                logger.error ( message );
                throw new Exception ( message );
            }
        }
        catch ( Exception e )
        {
            String message = String.format ( "Unable to execute command line arguments for printing! Detailed message: %1$s", e.getMessage () );
            logger.error ( message, e );
            throw new Exception ( message, e );
        }
    }

}
