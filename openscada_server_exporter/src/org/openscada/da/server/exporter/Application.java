/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.exporter;

import org.apache.log4j.Logger;

/**
 * The server exporter application starts a {@link Controller} with a specified configuration
 * file and lets it run 
 * @author Jens Reimann
 *
 */
public class Application
{
    private static Logger _log = Logger.getLogger ( Application.class );

    public static void main ( final String[] args ) throws Exception
    {
        String configurationFile = "configuration.xml";

        // use the provided config file name if we have one
        if ( args.length > 0 )
        {
            configurationFile = args[0];
        }

        _log.info ( "Loading configuration file: " + configurationFile );

        final Controller controller = new Controller ( configurationFile );
        controller.start ();

        _log.info ( "Exporter running..." );

        // Loop forever
        while ( true )
        {
            try
            {
                Thread.sleep ( 1000 );
            }
            catch ( final InterruptedException e )
            {
                _log.warn ( "Failed to sleep", e );
            }
        }
    }
}
