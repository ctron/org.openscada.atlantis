package org.openscada.hd.chart;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.swtchart.BackgroundOverlay;

public class BackgroundOverlayPainter implements BackgroundOverlay
{
    double[] data;

    double threshold = 0.0;

    final int padding = 10;

    private List<Rectangle> areasToDraw = new LinkedList<Rectangle> ();

    private Color color;
    
    private boolean invert = false;

    public void draw ( final GC gc, final int x, final int y )
    {
        if (data == null) {
            return;
        }
        areasToDraw.clear ();
        int i = 0;
        int start = -1;
        for ( double d : data )
        {
            if ( ( invert ? d > threshold : d < threshold ) && ( start == -1 ) )
            {
                start = i;
            }
            if ( ( invert ? d <= threshold : d >= threshold ) || ( i == data.length - 1 ) )
            {
                if ( start > -1 )
                {
                    int xcoord1 = (int) ( ( ( (double)start ) / ( (double)data.length ) ) * ( (double) ( x - padding * 2 ) ) ) + padding + 1;
                    int xcoord2 = (int) ( ( ( (double)i ) / ( (double)data.length ) ) * ( (double) ( x - padding * 2 ) ) ) + padding + 1;
                    areasToDraw.add ( new Rectangle ( xcoord1, 0, xcoord2 - xcoord1, y ) );
                }
                start = -1;
            }
            i += 1;
        }

        // save color
        int alpha = gc.getAlpha ();
        Color c = gc.getBackground ();
        gc.setAlpha ( 192 );
        gc.setBackground ( color );
        for ( Rectangle rectangle : areasToDraw )
        {
            gc.fillRectangle ( rectangle );
        }
        // restor color
        gc.setAlpha ( alpha );
        gc.setBackground ( c );
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
    
    public void setInvert ( boolean invert )
    {
        this.invert = invert;
    }
}
