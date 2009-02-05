package org.openscada.da.client.viewer.model.types;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

public class PointList
{
    private List<Point> _points = new LinkedList<Point> ();
    
    public void clear ()
    {
        _points.clear ();
    }
    
    public void addPoint ( Point p )
    {
        _points.add ( p );
    }
    
    public org.eclipse.draw2d.geometry.PointList toDraw2DPointList ()
    {
        org.eclipse.draw2d.geometry.PointList list = new org.eclipse.draw2d.geometry.PointList ();
        
        for ( Point p : _points )
        {
            list.addPoint ( p );
        }
        
        return list;
    }
    
    public List<Point> toList ()
    {
        return Collections.unmodifiableList ( _points );
    }
}
