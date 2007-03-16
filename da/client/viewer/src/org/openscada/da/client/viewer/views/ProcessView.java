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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.viewer.Activator;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.configurator.Configurator;
import org.openscada.da.client.viewer.configurator.xml.XMLConfigurator;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.View;
import org.openscada.da.client.viewer.model.impl.DisplaySynchronizedConnector;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PassThroughConnector;
import org.openscada.da.client.viewer.model.impl.containers.FigureContainer;
import org.openscada.da.client.viewer.model.impl.converter.ColorComposer;
import org.openscada.da.client.viewer.model.impl.converter.Double2IntegerConverter;
import org.openscada.da.client.viewer.model.impl.converter.FactorCalculator;
import org.openscada.da.client.viewer.model.impl.converter.Integer2DoubleConverter;
import org.openscada.da.client.viewer.model.impl.converter.ModuloCalculator;
import org.openscada.da.client.viewer.model.impl.converter.SimpleVariantIntegerConverter;
import org.openscada.da.client.viewer.model.impl.figures.Rectangle;
import org.openscada.da.client.viewer.model.impl.items.DataItemOutput;

public class ProcessView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.viewer.ProcessView";

    private static Logger _log = Logger.getLogger ( ProcessView.class );

    private Canvas _canvas = null;
    private LightweightSystem _system = null;

    private IFigure _rootFigure = null;

    private Container _container = null;

    public ProcessView ()
    {
    }

    protected void setView ( Container container )
    {
        if ( _container == null )
        {
            _container = container;
            createObjects ();
        }
    }

    @Override
    public void createPartControl ( Composite parent )
    {
        _canvas = new Canvas ( parent, SWT.NONE );
        _system = new LightweightSystem ( _canvas );

        _rootFigure = new Figure ();
        _rootFigure.setLayoutManager ( new XYLayout () );
        _rootFigure.setBackgroundColor ( ColorConstants.white );
        _rootFigure.setOpaque ( true );
        _system.setContents ( _rootFigure );

        createObjects ();
    }

    protected void createObjects ()
    {
        if ( _container == null )
        {
            return;
        }

        if ( _container instanceof DynamicUIObject )
        {
            ( (DynamicUIObject)_container ).createFigure ( _rootFigure );
        }
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
        if ( _container != null )
        {
            _log.debug ( "Disposing...container" );
            _container.dispose ();
            _container = null;
        }
        if ( _canvas != null )
        {
            _log.debug ( "Disposing...canvas" );
            _canvas.dispose ();
            _canvas = null;
        }
        _system = null;
        _container = null;
        super.dispose ();
    }

    public void setView ( String viewId ) throws XmlException, IOException, ConfigurationError
    {
        setView ( Activator.getDefault ().configureView ( viewId ));
    }

}
