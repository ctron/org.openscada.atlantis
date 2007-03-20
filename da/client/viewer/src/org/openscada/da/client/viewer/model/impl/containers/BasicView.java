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

package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.widgets.Canvas;
import org.openscada.da.client.viewer.model.View;

public class BasicView extends FigureContainer implements View
{
    private LightweightSystem _system = null;
    private IFigure _childPane = null;
    private ScrollPane _scrollPane = null;

    public BasicView ( String id )
    {
        super ( id );
    }

    @Override
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        // do nothing in the root element
    }

    public IFigure createRootFigure ( Canvas canvas, LightweightSystem system )
    {
        if ( _figure == null )
        {
            _system = system;
            _canvas = canvas;
            _figure = _scrollPane = new ScrollPane ();
            system.setContents ( _figure );
            
            _childPane = new Figure ();
            _childPane.setLayoutManager ( new XYLayout () );
            _childPane.setOpaque ( false );
            
            _scrollPane.setContents ( _childPane );
            
            createChildren ( _childPane );
            update ();
        }
        return _figure;
    }

    @Override
    public void dispose ()
    {
        if ( _system != null )
        {
            // dispose figure here so it will be ignored in the superclass
            _figure.remove ( _childPane );
            _figure = null;
            
            _childPane = null;
            
            _system.setContents ( null );
            _system = null;
        }
        super.dispose ();
    }
}
