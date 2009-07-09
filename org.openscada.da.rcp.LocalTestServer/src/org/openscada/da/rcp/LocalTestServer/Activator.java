/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.rcp.LocalTestServer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    private final static Logger logger = Logger.getLogger ( Activator.class );

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.da.rcp.LocalTestServer";

    public static final short SIM_PORT = 1202;

    public static final short TEST_PORT = 1203;

    // The shared instance
    private static Activator plugin;

    class Server
    {
        Exporter exporter = null;

        Exporter exporter2 = null;
    }

    private final Map<Integer, Server> exportMap = new HashMap<Integer, Server> ();

    /**
     * The constructor
     */
    public Activator ()
    {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop ( context );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor ( final String path )
    {
        return imageDescriptorFromPlugin ( PLUGIN_ID, path );
    }

    public void startLocalServer () throws Exception
    {
        synchronized ( this )
        {
            checkRunning ( TEST_PORT );
            final org.openscada.da.server.test.Hive testHive = new org.openscada.da.server.test.Hive ();
            testHive.start ();

            try
            {
                exportServer ( testHive, TEST_PORT );
            }
            catch ( final Throwable e )
            {
                logger.error ( "failed to start", e );
                notifyServerError ( e );
            }
        }
    }

    public void startLocalSimServer () throws Exception
    {
        synchronized ( this )
        {
            checkRunning ( SIM_PORT );
            final org.openscada.da.core.server.Hive hive = new org.openscada.da.server.simulation.component.Hive ();
            hive.start ();
            exportServer ( hive, SIM_PORT );
        }
    }

    private void checkRunning ( final int port ) throws AlreadyStartedException
    {
        if ( this.exportMap.containsKey ( port ) )
        {
            throw new AlreadyStartedException ();
        }
    }

    private void exportServer ( final Hive hive, final int port ) throws Exception
    {
        final Server server = new Server ();
        server.exporter = new Exporter ( hive, ConnectionInformation.fromURI ( "da:net://0.0.0.0:" + port ) );
        server.exporter.start ();
        server.exporter2 = new Exporter ( hive, ConnectionInformation.fromURI ( "da:net://0.0.0.0:" + port + "?socketImpl=VMPIPE" ) );
        server.exporter2.start ();
        this.exportMap.put ( port, server );
    }

    private void notifyServerError ( final Throwable t )
    {
        final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
        final IStatus status = new OperationStatus ( IStatus.ERROR, PLUGIN_ID, 0, "Server execution failed", t );

        if ( !shell.isDisposed () )
        {
            shell.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !shell.isDisposed () )
                    {
                        ErrorDialog.openError ( shell, null, "Server execution failed", status );
                    }
                }
            } );
        }
    }
}
