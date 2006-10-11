package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.openscada.da.client.viewer.model.DynamicUIObject;

public class Rectangle extends BaseFigure implements DynamicUIObject
{
    private RectangleFigure _rectangle = null;
    
    public Rectangle ( String id )
    {
        super ( id );
    }
    
    public IFigure getFigure ()
    {
        _rectangle = new RectangleFigure ();
        update ();
        return _rectangle;
    }

    public void dispose ()
    {
        _rectangle = null;
    }

    protected void update ()
    {
        updateFigure ( _rectangle );
    }

}
