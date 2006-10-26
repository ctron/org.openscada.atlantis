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

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.AliasedPropertyInput;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.Helper;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public abstract class BaseFigure extends BaseDynamicObject implements DynamicUIObject
{
    private static Logger _log = Logger.getLogger ( BaseFigure.class );
    
    private Color _color = null;
    private org.eclipse.draw2d.geometry.Rectangle _bounds = new org.eclipse.draw2d.geometry.Rectangle ( 0, 0, -1, -1 );

    public BaseFigure ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "color" ) );
        addInput ( new AliasedPropertyInput ( this, "width", "width" ) );
        addInput ( new AliasedPropertyInput ( this, "height", "height" ) );
        addInput ( new AliasedPropertyInput ( this, "x", "x" ) );
        addInput ( new AliasedPropertyInput ( this, "y", "y" ) );
        
        
    }
    
    public void setHeight ( Long height )
    {
        if ( height != null )
            _bounds.height = height.intValue ();
        update ();
    }
    
    public void setWidth ( Long width )
    {
        if ( width != null )
            _bounds.width = width.intValue ();
        update ();
    }
    
    public void setX ( Long x )
    {
        if ( x != null )
            _bounds.x = x.intValue ();
        update ();
    }

    public void setY ( Long y )
    {
        if ( y != null )
            _bounds.y = y.intValue ();
        update ();
    }

    public org.openscada.da.client.viewer.model.types.Color getColor ()
    {
        if ( _color == null )
            return null;
        else
            return Helper.colorFromRGB ( _color.getRGB () );
    }

    public void setColor ( org.openscada.da.client.viewer.model.types.Color color )
    {
        if ( color == null )
            _color = null;
        else
            _color = new Color ( Display.getCurrent (), Helper.colorToRGB ( color ) );
        update ();
    }

    public org.eclipse.draw2d.geometry.Rectangle getBounds ()
    {
        return _bounds;
    }

    public void setBounds ( org.eclipse.draw2d.geometry.Rectangle bounds )
    {
        _bounds = bounds;
    }
    
    protected void updateFigure ( IFigure figure )
    {
        if ( figure == null )
            return;
        
        //figure.setBounds ( _bounds );
        if ( figure.getParent () != null )
        {
            _log.debug ( String.format ( "Setting layout bounds: %d/%d/%d/%d", _bounds.x, _bounds.y, _bounds.width, _bounds.height ) );
            figure.getParent ().setConstraint ( figure, _bounds.getCopy () );
        }
        figure.setBackgroundColor ( _color );
    }
    
    protected abstract void update ();
}
