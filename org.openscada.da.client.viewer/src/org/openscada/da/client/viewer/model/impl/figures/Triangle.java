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
import org.eclipse.swt.widgets.Canvas;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Triangle extends BoundsFigure implements DynamicUIObject
{    
    private org.eclipse.draw2d.Triangle _triangle = null;
    private int _direction = 0;
    
    public Triangle ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "direction" ) );
    }
    
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        _triangle = new org.eclipse.draw2d.Triangle ();
        parent.add ( _triangle );
        
        update ();
    }
    
    public void dispose ()
    {
        if ( _triangle != null )
        {
            _triangle.getParent ().remove ( _triangle );
            _triangle = null;
        }
        super.dispose ();
    }
    
    protected void update ()
    {
        if ( _triangle == null )
            return;

        updateFigure ( _triangle );
        _triangle.setDirection ( _direction );
    }

    public Long getDirection ()
    {
        return (long)_direction;
    }

    public void setDirection ( Long direction )
    {
        _direction = direction.intValue ();
        update ();
    }

}
