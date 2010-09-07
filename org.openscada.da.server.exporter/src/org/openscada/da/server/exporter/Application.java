/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
