package org.openscada.da.utils.daemon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of the {@link DaemonController} interface for
 * local starting of {@link Daemon} implementations.
 * @author Jens Reimann
 *
 */
public class DaemonStarter implements DaemonController
{

    private static final Logger logger = LoggerFactory.getLogger ( DaemonStarter.class );

    public static void main ( final String[] args ) throws Exception
    {
        if ( args.length == 0 )
        {
            throw new RuntimeException ( "syntax: DaemonStarter <daemon class name>" );
        }

        Queue<String> argList = new LinkedList<String> ();
        argList.addAll ( Arrays.asList ( args ) );

        new DaemonStarter ( Class.forName ( argList.poll () ), argList.toArray ( new String[0] ) );

        while ( true )
        {
            Thread.sleep ( 1000 );
        }
    }

    private final Daemon daemon;

    public DaemonStarter ( final Class<?> className, final String[] args ) throws Exception
    {
        Object o = className.newInstance ();
        if ( ! ( o instanceof Daemon ) )
        {
            throw new RuntimeException ( String.format ( "Class must implement '%s'", Daemon.class ) );
        }

        final DaemonController controller = this;
        this.daemon = (Daemon)o;
        this.daemon.init ( new DaemonContext () {

            public String[] getArguments ()
            {
                return args;
            }

            public DaemonController getController ()
            {
                return controller;
            }
        } );
        this.daemon.start ();

    }

    public void fail () throws IllegalStateException
    {
        logger.error ( "Service failed" );
        System.exit ( -1 );
    }

    public void fail ( final String arg0 ) throws IllegalStateException
    {
        logger.error ( "Service failed: " + arg0 );
        System.exit ( -1 );
    }

    public void fail ( final Exception arg0 ) throws IllegalStateException
    {
        logger.error ( "Service failed", arg0 );
    }

    public void fail ( final String arg0, final Exception arg1 ) throws IllegalStateException
    {
        logger.error ( String.format ( "Service failed: '%s'", arg0 ), arg1 );
    }

    public void reload () throws IllegalStateException
    {
        try
        {
            this.daemon.stop ();
            this.daemon.start ();
        }
        catch ( Exception e )
        {
            fail ( "Failed to reload", e );
        }
    }

    public void shutdown () throws IllegalStateException
    {
        if ( this.daemon != null )
        {
            try
            {
                this.daemon.stop ();
            }
            catch ( Exception e )
            {
                fail ( "Failed to shut down", e );
            }
        }
        System.exit ( 0 );
    }
}
