package org.openscada.da.utils.daemon;

import java.io.File;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.openscada.da.server.exporter.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapping the exporter controller into a jakarta daemon
 * @see Daemon 
 * @author Jens Reimann
 *
 */
public class ExporterDaemon implements Daemon
{

    private static final Logger logger = LoggerFactory.getLogger ( ExporterDaemon.class );

    private Controller controller;

    public void destroy ()
    {
        this.controller = null;
    }

    public void init ( final DaemonContext ctx ) throws Exception
    {
        logger.info ( "Initializing ExporterDaemon" );

        if ( ctx.getArguments ().length < 1 )
        {
            logger.warn ( "No arguments passed. No config file available" );
            throw new Exception ( "Invalid arguments: exporter <configfile>" );
        }

        String fileName = ctx.getArguments ()[0];
        logger.info ( "Loading configuration from: {}", fileName );
        File configFile = new File ( fileName );
        if ( !configFile.canRead () )
        {
            throw new Exception ( String.format ( "'%s' is not a file or can not be read", fileName ) );
        }

        this.controller = new Controller ( configFile );
    }

    public void start () throws Exception
    {
        logger.info ( "Starting ExporterDaemon" );
        this.controller.start ();
    }

    public void stop () throws Exception
    {
        logger.info ( "Stopping service" );
        this.controller.stop ();
    }

}
