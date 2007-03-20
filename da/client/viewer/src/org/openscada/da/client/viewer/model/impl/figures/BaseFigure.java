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

import java.beans.PropertyEditorManager;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.Helper;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public abstract class BaseFigure extends BaseDynamicObject implements DynamicUIObject
{
    private static Logger _log = Logger.getLogger ( BaseFigure.class );

    static
    {

        try
        {
            PropertyEditorManager.registerEditor ( Integer.class, Class.forName ( "sun.beans.editors.IntEditor" ) );
        }
        catch ( Exception e )
        {
        }
    }

    private Color _color = null;
    private Color _backgroundColor = null;
    private boolean _opaque = false;
    private org.eclipse.draw2d.geometry.Rectangle _bounds = new org.eclipse.draw2d.geometry.Rectangle ( 0, 0, -1, -1 );

    private Integer _fontSize = null;

    private Font _font = null;

    public BaseFigure ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "color" ) );
        addInput ( new PropertyInput ( this, "backgroundColor" ) );
        addInput ( new PropertyInput ( this, "width" ) );
        addInput ( new PropertyInput ( this, "height" ) );
        addInput ( new PropertyInput ( this, "x" ) );
        addInput ( new PropertyInput ( this, "y" ) );
        addInput ( new PropertyInput ( this, "opaque" ) );
    }

    @Override
    public void dispose ()
    {
        disposeFont ();
        super.dispose ();
    }

    private void disposeFont ()
    {
        if ( _font != null )
        {
            _font.dispose ();
            _font = null;
        }
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

    public org.openscada.da.client.viewer.model.types.Color getBackgroundColor ()
    {
        if ( _backgroundColor == null )
            return null;
        else
            return Helper.colorFromRGB ( _backgroundColor.getRGB () );
    }

    public void setColor ( org.openscada.da.client.viewer.model.types.Color color )
    {
        if ( color == null )
            _color = null;
        else
            _color = new Color ( Display.getCurrent (), Helper.colorToRGB ( color ) );
        update ();
    }

    public void setBackgroundColor ( org.openscada.da.client.viewer.model.types.Color backgroundColor )
    {
        if ( backgroundColor == null )
            _backgroundColor = null;
        else
            _backgroundColor = new Color ( Display.getCurrent (), Helper.colorToRGB ( backgroundColor ) );
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
        {
            return;
        }

        //figure.setBounds ( _bounds );
        if ( figure.getParent () != null )
        {
            _log.debug ( String.format ( getId () + ": Setting layout bounds: %d/%d/%d/%d", _bounds.x, _bounds.y, _bounds.width,
                    _bounds.height ) );
            figure.getParent ().setConstraint ( figure, _bounds.getCopy () );
        }
        figure.setForegroundColor ( _color );
        figure.setBackgroundColor ( _backgroundColor );
        figure.setOpaque ( _opaque );

        updateFont ( figure );
        figure.setFont ( _font );
    }

    protected abstract void update ();

    public boolean isOpaque ()
    {
        return _opaque;
    }

    public void setOpaque ( boolean opaque )
    {
        _opaque = opaque;
        update ();
    }

    protected void updateFont ( IFigure figure )
    {
        if ( _font != null )
        {
            return;
        }

        if ( _fontSize == null )
        {
            return;
        }

        // build our font
        Font parentFont = figure.getParent ().getFont ();
        FontData[] fds = parentFont.getFontData ();

        for ( FontData fontData : fds )
        {
            if ( _fontSize != null )
            {
                fontData.setHeight ( _fontSize );
            }
        }

        _font = new Font ( parentFont.getDevice (), fds );
    }

    public void setFontSize ( Integer fontSize )
    {
        _fontSize = fontSize;
        disposeFont ();
        update ();
    }
}
