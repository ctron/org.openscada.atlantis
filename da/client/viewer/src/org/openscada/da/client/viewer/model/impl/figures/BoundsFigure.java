package org.openscada.da.client.viewer.model.impl.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public abstract class BoundsFigure extends BaseFigure
{
    private static Logger _log = Logger.getLogger ( BoundsFigure.class );

    private org.eclipse.draw2d.geometry.Rectangle _bounds = new org.eclipse.draw2d.geometry.Rectangle ( 0, 0, -1, -1 );
    
    public BoundsFigure ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "width" ) );
        addInput ( new PropertyInput ( this, "height" ) );
        addInput ( new PropertyInput ( this, "x" ) );
        addInput ( new PropertyInput ( this, "y" ) );
    }

    @Override
    protected void updateFigure ( IFigure figure )
    {
        super.updateFigure ( figure );

        if ( figure.getParent () != null )
        {
            _log.debug ( String.format ( getId () + ": Setting layout bounds: %d/%d/%d/%d", _bounds.x, _bounds.y, _bounds.width,
                    _bounds.height ) );
            figure.getParent ().setConstraint ( figure, _bounds.getCopy () );
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
    

    public org.eclipse.draw2d.geometry.Rectangle getBounds ()
    {
        return _bounds;
    }

    public void setBounds ( org.eclipse.draw2d.geometry.Rectangle bounds )
    {
        _bounds = bounds;
    }


}
