/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.test;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin
{
    private static Logger _log = Logger.getLogger ( "org.openscada.da.client.test.Plugin" );

    public static final String PLUGIN_ID = "org.openscada.da.client.test";

    //The shared instance.
    private static Activator plugin = null;

    /**
     * The constructor.
     */
    public Activator ()
    {
        plugin = this;
    }

    @Override
    protected void initializeImageRegistry ( final ImageRegistry reg )
    {
        super.initializeImageRegistry ( reg );

        getImageRegistry ().put ( ISharedImages.IMG_HIVE_CONNECTION, getImageDescriptor ( "icons/stock_channel.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_CONNECTED, getImageDescriptor ( "icons/stock_connect.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_DISCONNECTED, getImageDescriptor ( "icons/stock_disconnect.png" ) );

        getImageRegistry ().put ( ISharedImages.IMG_HIVE_ITEM, getImageDescriptor ( "icons/16x16/stock_dataitem.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_ITEM_I, getImageDescriptor ( "icons/16x16/stock_dataitem_i.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_ITEM_O, getImageDescriptor ( "icons/16x16/stock_dataitem_o.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_ITEM_IO, getImageDescriptor ( "icons/16x16/stock_dataitem_io.png" ) );

        getImageRegistry ().put ( ISharedImages.IMG_HIVE_FOLDER, getImageDescriptor ( "icons/16x16/stock_folder.png" ) );
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        super.stop ( context );
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor ( final String path )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin ( "org.openscada.da.client.test", path );
    }

    public static String getId ()
    {
        return getDefault ().getBundle ().getSymbolicName ();
    }

    public static void logError ( final int code, final String msg, final Throwable ex )
    {
        getDefault ().getLog ().log ( new Status ( IStatus.ERROR, getId (), code, msg, ex ) );
    }

    /**
     * Notify error using message box (thread safe).
     * @param message The message to display
     * @param error The error that occurred
     */
    public void notifyError ( final String message, final Throwable error )
    {
        final Display display = getWorkbench ().getDisplay ();

        if ( !display.isDisposed () )
        {
            display.asyncExec ( new Runnable () {

                public void run ()
                {
                    final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
                    _log.debug ( String.format ( "Shell disposed: %s", shell.isDisposed () ) );
                    if ( !shell.isDisposed () )
                    {
                        final IStatus status = new OperationStatus ( OperationStatus.ERROR, PLUGIN_ID, 0, message + ":" + error.getMessage (), error );
                        ErrorDialog.openError ( shell, null, message, status );
                    }
                }
            } );
        }
    }
}
