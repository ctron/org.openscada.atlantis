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
 * 
 * @author Jens Reimann
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

        final Queue<String> argList = new LinkedList<String> ();
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
        final Object o = className.newInstance ();
        if ( ! ( o instanceof Daemon ) )
        {
            throw new RuntimeException ( String.format ( "Class must implement '%s'", Daemon.class ) );
        }

        final DaemonController controller = this;
        this.daemon = (Daemon)o;
        this.daemon.init ( new DaemonContext () {

            @Override
            public String[] getArguments ()
            {
                return args;
            }

            @Override
            public DaemonController getController ()
            {
                return controller;
            }
        } );
        this.daemon.start ();

    }

    @Override
    public void fail () throws IllegalStateException
    {
        logger.error ( "Service failed" );
        System.exit ( -1 );
    }

    @Override
    public void fail ( final String arg0 ) throws IllegalStateException
    {
        logger.error ( "Service failed: " + arg0 );
        System.exit ( -1 );
    }

    @Override
    public void fail ( final Exception arg0 ) throws IllegalStateException
    {
        logger.error ( "Service failed", arg0 );
    }

    @Override
    public void fail ( final String arg0, final Exception arg1 ) throws IllegalStateException
    {
        logger.error ( String.format ( "Service failed: '%s'", arg0 ), arg1 );
    }

    @Override
    public void reload () throws IllegalStateException
    {
        try
        {
            this.daemon.stop ();
            this.daemon.start ();
        }
        catch ( final Exception e )
        {
            fail ( "Failed to reload", e );
        }
    }

    @Override
    public void shutdown () throws IllegalStateException
    {
        if ( this.daemon != null )
        {
            try
            {
                this.daemon.stop ();
            }
            catch ( final Exception e )
            {
                fail ( "Failed to shut down", e );
            }
        }
        System.exit ( 0 );
    }
}
