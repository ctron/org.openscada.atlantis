package org.openscada.hd.chart;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.swtchart.BackgroundOverlay;

public abstract class BaseBackgroundOverlay implements BackgroundOverlay
{
    double[] data;

    double threshold = 0.0;

    List<Rectangle> areasToDraw = new LinkedList<Rectangle> ();

    Color color;

    public void draw ( final GC gc, final int x, final int y )
    {
        areasToDraw.clear ();
        areasToDraw.add ( new Rectangle ( 100, 0, 100, y ) );
        gc.setAlpha ( 128 );
        gc.setForeground ( color );
        for ( Rectangle rectangle : areasToDraw )
        {
            gc.drawRectangle ( rectangle );
        }
    }

    public void setData ( final double[] data )
    {
        this.data = data;
    }

    public void setThreshold ( final double threshold )
    {
        this.threshold = threshold;
    }

    public void setColor ( final Color color )
    {
        this.color = color;
    }
}
