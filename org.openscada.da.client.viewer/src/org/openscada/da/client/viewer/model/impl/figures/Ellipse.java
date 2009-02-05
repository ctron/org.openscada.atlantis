package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Canvas;

public class Ellipse extends Shape
{

    private org.eclipse.draw2d.Ellipse _ellipse = null;
    
    public Ellipse ( String id )
    {
        super ( id );
    }

    @Override
    protected void update ()
    {
        if ( _ellipse != null )
        {
            updateFigure ( _ellipse );
        }
    }
    
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _ellipse == null )
        {
            _ellipse = new org.eclipse.draw2d.Ellipse ();
            parent.add ( _ellipse );
            update ();
        }
    }
    
    @Override
    public void dispose ()
    {
        if ( _ellipse != null )
        {
            _ellipse.getParent ().remove ( _ellipse );
            _ellipse = null;
        }
        super.dispose ();
    }

}
