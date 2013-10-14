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

package org.eclipse.scada.da.utils.daemon;

import java.io.File;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.eclipse.scada.da.server.exporter.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapping the exporter controller into a jakarta daemon
 * 
 * @see Daemon
 * @author Jens Reimann
 */
public class ExporterDaemon implements Daemon
{

    private static final Logger logger = LoggerFactory.getLogger ( ExporterDaemon.class );

    private Controller controller;

    @Override
    public void destroy ()
    {
        this.controller = null;
    }

    @Override
    public void init ( final DaemonContext ctx ) throws Exception
    {
        logger.info ( "Initializing ExporterDaemon" );

        if ( ctx.getArguments ().length < 1 )
        {
            logger.warn ( "No arguments passed. No config file available" );
            throw new Exception ( "Invalid arguments: exporter <configfile>" );
        }

        final String fileName = ctx.getArguments ()[0];
        logger.info ( "Loading configuration from: {}", fileName );
        final File configFile = new File ( fileName );
        if ( !configFile.canRead () )
        {
            throw new Exception ( String.format ( "'%s' is not a file or can not be read", fileName ) );
        }

        this.controller = new Controller ( configFile );
    }

    @Override
    public void start () throws Exception
    {
        logger.info ( "Starting ExporterDaemon" );
        this.controller.start ();
    }

    @Override
    public void stop () throws Exception
    {
        logger.info ( "Stopping service" );
        this.controller.stop ();
    }

}
