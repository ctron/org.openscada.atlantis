package org.openscada.da.client.viewer.model.impl;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.viewer.model.DynamicUIObject;

public class Rectangle extends BaseDynamicObject implements DynamicUIObject
{

    private Color _color = null;
    private org.eclipse.draw2d.geometry.Rectangle _bounds = new org.eclipse.draw2d.geometry.Rectangle ( 0, 0, 0, 0 );
    
    private RectangleFigure _rectangle = null;
    
    public Rectangle ()
    {
        addInput ( new AliasedPropertyInput ( this, "color", "color" ) );
        addInput ( new AliasedPropertyInput ( this, "width", "width" ) );
        addInput ( new AliasedPropertyInput ( this, "height", "height" ) );
        addInput ( new AliasedPropertyInput ( this, "x", "x" ) );
        addInput ( new AliasedPropertyInput ( this, "y", "y" ) );
    }
    
    public IFigure createFigure ()
    {
        _rectangle = new RectangleFigure ();
        update ();
        return _rectangle;
    }

    public RGB getColor ()
    {
        return _color.getRGB ();
    }

    public void setColor ( RGB color )
    {
        _color = new Color ( Display.getCurrent (), color );
        update ();
    }

    public void dispose ()
    {
        _rectangle = null;
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
    
    protected void update ()
    {
        if ( _rectangle == null )
            return;
        
        _rectangle.setBounds ( _bounds );
        
        if ( _color != null )
        {
            _rectangle.setBackgroundColor ( _color );
        }
    }

}
