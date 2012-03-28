/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server exporter application starts a {@link Controller} with a specified configuration
 * file and lets it run 
 * @author Jens Reimann
 *
 */
public class Application
{

    private final static Logger logger = LoggerFactory.getLogger ( Application.class );

    private static volatile boolean running = true;

    private static volatile boolean active = false;

    private static Object activeLock = new Object ();

    public static void main ( final String[] args ) throws Exception
    {
        active = true;

        String configurationFile = "configuration.xml";

        // use the provided config file name if we have one
        if ( args.length > 0 )
        {
            configurationFile = args[0];
        }

        logger.info ( "Loading configuration file: {}", configurationFile );

        final Controller controller = new Controller ( configurationFile );
        controller.start ();

        logger.info ( "Exporter running..." );

        // Loop forever ... maybe
        while ( running )
        {
            try
            {
                Thread.sleep ( 1000 );
            }
            catch ( final InterruptedException e )
            {
                logger.warn ( "Failed to sleep", e );
                Thread.interrupted ();
            }
        }

        logger.info ( "Stopping controller" );
        controller.stop ();
        logger.info ( "Stop complete" );

        synchronized ( activeLock )
        {
            logger.info ( "Signal shutdown" );
            active = false;
            activeLock.notifyAll ();
        }
    }

    /**
     * shutdown method for advanced installers stop mechanism
     */
    public static void stop ()
    {
        running = false;
        final long timeout = Long.getLong ( "org.openscada.da.server.exporter.shutdownPeriod", 15 * 1000 );
        final long start = System.currentTimeMillis ();
        synchronized ( activeLock )
        {
            while ( active )
            {
                if ( System.currentTimeMillis () - start > timeout )
                {
                    logger.warn ( "Timeout wainting for shutdown. Force exit!" );
                    System.exit ( 0 );
                    return;
                }
                try
                {
                    activeLock.wait ( 1000 );
                }
                catch ( final InterruptedException e )
                {
                    logger.warn ( "Failed to wait for shutdown", e );
                    return;
                }
                logger.info ( "Still waiting for shutdown..." );
            }
        }
    }
}
