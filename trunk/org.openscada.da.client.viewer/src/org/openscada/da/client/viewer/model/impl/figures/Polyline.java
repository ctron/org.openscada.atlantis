package org.openscada.da.client.viewer.model.impl.figures;



public class Polyline extends PolylineBaseFigure
{
    public Polyline ( String id )
    {
        super ( id );
    }
    
    public void setPointList ( org.openscada.da.client.viewer.model.types.PointList points )
    {
        setPoints ( points.toDraw2DPointList () );
        update ();
    }
}
