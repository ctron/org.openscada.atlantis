package org.openscada.da.client.viewer.model.impl.figures;

import org.openscada.da.client.viewer.model.impl.PropertyInput;

public abstract class Shape extends BoundsFigure
{
    private boolean _fill = false;
    private boolean _fillXOR = false;
    private int _lineWidth = 1;
    private boolean _outline = true;
    private boolean _outlineXOR = false;
    
    public Shape ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "fill" ) );
        addInput ( new PropertyInput ( this, "fillXOR" ) );
        addInput ( new PropertyInput ( this, "outline" ) );
        addInput ( new PropertyInput ( this, "outlineXOR" ) );
        addInput ( new PropertyInput ( this, "lineWidth" ) );
    }
    
    protected void updateFigure ( org.eclipse.draw2d.Shape shape )
    {
        super.updateFigure ( shape );
        shape.setFill ( _fill );
        shape.setFillXOR ( _fillXOR );
        shape.setLineWidth ( _lineWidth );
        shape.setOutline ( _outline );
        shape.setOutlineXOR ( _outlineXOR );
    }

    public boolean isFill ()
    {
        return _fill;
    }

    public void setFill ( boolean fill )
    {
        _fill = fill;
        update ();
    }

    public boolean isFillXOR ()
    {
        return _fillXOR;
    }

    public void setFillXOR ( boolean fillXOR )
    {
        _fillXOR = fillXOR;
        update ();
    }

    public int getLineWidth ()
    {
        return _lineWidth;
    }

    public void setLineWidth ( int lineWidth )
    {
        _lineWidth = lineWidth;
        update ();
    }

    public boolean isOutline ()
    {
        return _outline;
    }

    public void setOutline ( boolean outline )
    {
        _outline = outline;
        update ();
    }

    public boolean isOutlineXOR ()
    {
        return _outlineXOR;
    }

    public void setOutlineXOR ( boolean outlineXOR )
    {
        _outlineXOR = outlineXOR;
        update ();
    }

}