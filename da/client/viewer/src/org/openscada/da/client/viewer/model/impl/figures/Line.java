package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Line extends PolylineBaseFigure
{
    private Point p1 = new Point ();
    private Point p2 = new Point ();

    public Line ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "x1" ) );
        addInput ( new PropertyInput ( this, "y1" ) );
        addInput ( new PropertyInput ( this, "x2" ) );
        addInput ( new PropertyInput ( this, "y2" ) );
    }
    
    protected void updatePoints ()
    {   
        PointList p = new PointList ();
        p.addPoint ( p1.getCopy () );
        p.addPoint ( p2.getCopy () );
        setPoints ( p );
        update ();
    } 
    
    public void setX1 ( int value )
    {
        p1.x = value;
        updatePoints ();
    }
    
    public void setX2 ( int value )
    {
        p2.x = value;
        updatePoints ();
    }
    
    public void setY1 ( int value )
    {
        p1.y = value;
        updatePoints ();
    }
    
    public void setY2 ( int value )
    {
        p2.y = value;
        updatePoints ();
    }
}
