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
    
    public void createFigure ( IFigure parent )
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
    }

    protected void update ()
    {
        updateFigure ( _rectangle );
    }

}
