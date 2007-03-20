/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client.viewer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.configurator.Configurator;
import org.openscada.da.client.viewer.configurator.xml.XMLConfigurator;
import org.openscada.da.client.viewer.model.View;
import org.openscada.da.client.viewer.views.ProcessView;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    private static Logger _log = Logger.getLogger ( Activator.class );

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.da.client.viewer";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator ()
    {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext context ) throws Exception
    {
        super.start ( context );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
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
    
    protected InputStream getSampleView () throws IOException
    {
        return FileLocator.openStream ( getBundle (), new Path ( "view.xml" ), true );
    }
    
    public View configureView ( String viewId ) throws XmlException, IOException, ConfigurationError
    {
        Configurator cfg = new XMLConfigurator ( Activator.getDefault ().getSampleView () );
        return cfg.configure ( viewId );
    }
    
    public void openProcessView ( String viewId )
    {
        try
        {
            ProcessView processView = (ProcessView)getWorkbench ().getActiveWorkbenchWindow ().getActivePage ().showView ( ProcessView.VIEW_ID, viewId, IWorkbenchPage.VIEW_ACTIVATE );
            processView.setView ( viewId );
            _log.debug ( "View open: " + viewId );
        }
        catch ( Exception e )
        {
            _log.debug ( "Failed to open view", e );
            ErrorDialog.openError ( getWorkbench ().getActiveWorkbenchWindow ().getShell (), "Error", "Failed to open process view", new Status ( Status.ERROR, Activator.PLUGIN_ID, 0, "Failed to open process view", e ) );
        }
    }

}
