package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Triangle extends BaseFigure implements DynamicUIObject
{    
    private org.eclipse.draw2d.Triangle _triangle = null;
    private int _direction = 0;
    
    public Triangle ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "direction" ) );
    }
    
    public void createFigure ( IFigure parent )
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
