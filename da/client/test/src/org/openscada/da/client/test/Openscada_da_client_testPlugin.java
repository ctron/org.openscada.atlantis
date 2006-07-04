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

package org.openscada.da.client.test;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveRepository;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Openscada_da_client_testPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.openscada.da.client.test";
    
	//The shared instance.
	private static Openscada_da_client_testPlugin plugin = null;
    
	/**
	 * The constructor.
	 */
	public Openscada_da_client_testPlugin() {
		plugin = this;
	}
    
    @Override
    protected void initializeImageRegistry ( ImageRegistry reg )
    {
        super.initializeImageRegistry ( reg );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTION, getImageDescriptor ( "icons/stock_channel.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTED, getImageDescriptor ( "icons/stock_connect.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_DISCONNECTED, getImageDescriptor ( "icons/stock_disconnect.png" ) );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_ITEM, getImageDescriptor ( "icons/16x16/stock_dataitem.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_ITEM_I, getImageDescriptor ( "icons/16x16/stock_dataitem_i.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_ITEM_O, getImageDescriptor ( "icons/16x16/stock_dataitem_o.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_ITEM_IO, getImageDescriptor ( "icons/16x16/stock_dataitem_io.png" ) );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_FOLDER, getImageDescriptor ( "icons/16x16/stock_folder.png" ) );
    }

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Openscada_da_client_testPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.openscada.da.client.test", path);
	}
    
    public static String getId()
    {
        return getDefault().getBundle().getSymbolicName();
    }
    
    public static void logError ( int code, String msg, Throwable ex )
    {
        getDefault().getLog().log(new Status(IStatus.ERROR, getId(), code, msg, ex));
    }
    
    private static HiveRepository _repository = null;
    public static HiveRepository getRepository ()
    {
        if ( _repository == null )
        {
            _repository = new HiveRepository();
            
            IPath hives = getRepostoryFile();
            if ( hives.toFile().canRead() )
                _repository.load(hives);
            else
            {
                HiveConnectionInformation connection = new HiveConnectionInformation();
                connection.setHost ( "localhost" );
                connection.setPort ( 1202 );
                _repository.getConnections().add(new HiveConnection(connection));
                _repository.save(hives);
            }
            
            
        }
        return _repository;
    }
    public static IPath getRepostoryFile ()
    {
        return getDefault().getStateLocation().append("hives.xml");
    }

    public void notifyError ( final String message, final Throwable error )
    {
        
        final Display display = getWorkbench ().getDisplay ();
        
        if ( !display.isDisposed () )
        {
            display.asyncExec ( new Runnable() {

                public void run ()
                {
                    Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
                    if ( !shell.isDisposed () )
                    {
                        IStatus status = new OperationStatus ( OperationStatus.ERROR, PLUGIN_ID, 0, error.getMessage (), error );
                        ErrorDialog.openError ( shell, null, message, status );
                    }
                }} );
        }
    }
}
