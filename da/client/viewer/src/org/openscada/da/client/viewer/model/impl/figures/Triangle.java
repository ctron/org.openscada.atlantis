package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Triangle extends BaseFigure implements DynamicUIObject
{    
    private org.eclipse.draw2d.Triangle _triangle = null;
    private int _direction = 0;
    
    public Triangle ()
    {
        super ();
        addInput ( new PropertyInput ( this, "direction" ) );
    }
    
    public IFigure createFigure ()
    {
        _triangle = new org.eclipse.draw2d.Triangle  ();
        
        update ();
        return _triangle;
    }
    
    public void dispose ()
    {
        _triangle = null;
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
