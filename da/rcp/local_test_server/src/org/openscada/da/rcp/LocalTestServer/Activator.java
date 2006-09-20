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

package org.openscada.da.rcp.LocalTestServer;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.da.core.common.configuration.ConfigurationError;
import org.openscada.da.server.net.Exporter;
import org.openscada.da.server.test.Hive;
import org.osgi.framework.BundleContext;

import com.sun.org.apache.bcel.internal.generic.ATHROW;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.openscada.da.rcp.LocalTestServer";

	// The shared instance
	private static Activator plugin;
    
    private org.openscada.da.server.net.Exporter _exporter = null;
    private Thread _exporterThread = null;
	
	/**
	 * The constructor
	 */
	public Activator()
    {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
    {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
    {
		plugin = null;
		super.stop(context);
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
	public static ImageDescriptor getImageDescriptor(String path)
    {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
    
    public void startLocalServer () throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, AlreadyStartedException, ConfigurationError, XmlException
    {
        synchronized ( this )
        {
            if ( _exporter != null )
                throw new AlreadyStartedException();
            
            Hive testHive = new Hive ( new Path ( "hive.xml" ).toFile () );
            
            _exporter = new Exporter ( testHive );
            
            _exporterThread = new Thread ( new Runnable () {

                public void run ()
                {
                    try
                    {
                        _exporter.run ();
                    }
                    catch ( Exception e )
                    {
                        notifyServerError ( e );
                        _exporter = null;
                        _exporterThread = null;
                    }
                }} );
            _exporterThread.setDaemon ( true );
            _exporterThread.start ();
        }
    }
    
    private void notifyServerError ( Throwable t )
    {
        final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
        final IStatus status = new OperationStatus ( OperationStatus.ERROR, PLUGIN_ID, 0, "Server execution failed", t );
        
        if ( !shell.isDisposed () )
        {
            shell.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !shell.isDisposed () )
                    {
                        ErrorDialog.openError ( shell, null, "Server execution failed", status );
                    }
                }} );
        }
    }
}
