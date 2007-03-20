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

package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.widgets.Canvas;
import org.openscada.da.client.viewer.model.DynamicUIObject;

public class Rectangle extends Shape implements DynamicUIObject
{
    private RectangleFigure _rectangle = null;
    
    public Rectangle ( String id )
    {
        super ( id );
    }
    
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        _rectangle = new RectangleFigure ();
        parent.add ( _rectangle );
        update ();
    }

    public void dispose ()
    {
        if ( _rectangle != null )
        {
            _rectangle.getParent ().remove ( _rectangle );
            _rectangle = null;
        }
        super.dispose ();
    }

    protected void update ()
    {
        if ( _rectangle != null )
        {
            updateFigure ( _rectangle );
        }
    }

}
