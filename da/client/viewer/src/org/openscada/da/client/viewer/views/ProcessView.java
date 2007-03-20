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

package org.openscada.da.client.viewer.views;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.viewer.Activator;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.View;
import org.openscada.da.client.viewer.model.impl.containers.BasicView;

public class ProcessView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.viewer.ProcessView";

    private static Logger _log = Logger.getLogger ( ProcessView.class );

    private Canvas _canvas = null;
    private LightweightSystem _system = null;
    private BasicView _view = null;

    public ProcessView ()
    {
    }

    protected void setView ( View view )
    {
        if ( _view == null )
        {
            if ( view instanceof BasicView )
            {
                BasicView basicView = (BasicView)view;
                basicView.createRootFigure ( _canvas, _system );
                _view = basicView;
            }
            else
            {
                String message = String.format ( "View '%s' is not a main view that is of type '%s' (view is of type '%s')", view.getId (), BasicView.class, view.getClass () );
                ErrorDialog.openError ( _canvas.getShell (), "Error", message, new Status ( Status.ERROR, Activator.PLUGIN_ID, 0, message, null ) );
            }
        }
    }

    @Override
    public void createPartControl ( Composite parent )
    {
        _canvas = new Canvas ( parent, SWT.NONE );
        _system = new LightweightSystem ( _canvas );
    }

    @Override
    public void setFocus ()
    {
        _canvas.setFocus ();
    }

    @Override
    public void dispose ()
    {
        _log.debug ( "Disposing..." );
        if ( _view != null )
        {
            _view.dispose ();
            _view = null;
        }
        _system = null;
        super.dispose ();
    }

    public void setView ( String viewId ) throws XmlException, IOException, ConfigurationError
    {
        setView ( Activator.getDefault ().configureView ( viewId ) );
    }

}
