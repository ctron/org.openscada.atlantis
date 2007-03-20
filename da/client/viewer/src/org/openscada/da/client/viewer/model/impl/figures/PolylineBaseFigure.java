package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.widgets.Canvas;


public class PolylineBaseFigure extends BaseFigure
{
    private Polyline _polyline = null;
    private PointList _points = new PointList ();
    private int _lineWidth = 1;

    public PolylineBaseFigure ( String id )
    {
        super ( id );
    }
    
    @Override
    protected void update ()
    {
        if ( _polyline != null && _points != null )
        {
            updateFigure ( _polyline );
        }
    }
    
    protected void updateFigure ( Polyline polyline )
    {
        super.updateFigure ( polyline );
        polyline.setPoints ( _points );
        polyline.setLineWidth ( _lineWidth );
    }
    
    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _polyline == null )
        {
            _polyline = new Polyline ();
            parent.add ( _polyline );
            update ();
        }
    }
    
    protected void setPoints ( PointList points )
    {
        _points = points;
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
}
