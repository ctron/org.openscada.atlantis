package org.openscada.hd.chart;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;

public class TrendChart extends Chart implements PaintListener
{
    volatile int currentX = -1;

    volatile int currentY = -1;

    volatile boolean showInfo = false;

    private DataAtPoint dataAtPoint;

    public DataAtPoint getDataAtPoint ()
    {
        return dataAtPoint;
    }

    public void setDataAtPoint ( final DataAtPoint dataAtPoint )
    {
        this.dataAtPoint = dataAtPoint;
    }

    /**
     * @param parent
     * @param style
     */
    public TrendChart ( final Composite parent, final int style )
    {
        super ( parent, style );
        this.getPlotArea ().addMouseMoveListener ( new MouseMoveListener () {
            public void mouseMove ( final MouseEvent e )
            {
                showInfo = false;
                currentX = e.x;
                currentY = e.y;
                TrendChart.this.redraw ();
            }
        } );
        this.getPlotArea ().addMouseTrackListener ( new MouseTrackListener () {
            public void mouseHover ( final MouseEvent e )
            {
                showInfo = true;
                TrendChart.this.redraw ();
            }

            public void mouseExit ( final MouseEvent e )
            {
                currentX = -1;
                currentY = -1;
                TrendChart.this.redraw ();
            }

            public void mouseEnter ( final MouseEvent e )
            {
                currentX = e.x;
                currentY = e.y;
                TrendChart.this.redraw ();
            }
        } );
        this.getPlotArea ().addPaintListener ( this );
    }

    public void paintControl ( final PaintEvent e )
    {
        GC gc = e.gc;
        if ( currentX > -1 )
        {
            gc.drawLine ( currentX, 0, currentX, this.getPlotArea ().getBounds ().height - 1 );
        }
        if ( showInfo )
        {
            drawInfo ( gc );
        }
    }

    private void drawInfo ( final GC gc )
    {
        if ( dataAtPoint != null )
        {
            double quality = dataAtPoint.getQuality ( currentX );
            Date timestamp = dataAtPoint.getTimestamp ( currentX );
            Map<String, Double> data = dataAtPoint.getData ( currentX );
            gc.setAntialias ( SWT.ON );
            int xoffset = 10;
            int yoffset = 10;
            int corner = 10;
            int padding = 5;
            gc.setBackground ( getDisplay ().getSystemColor ( SWT.COLOR_INFO_BACKGROUND ) );
            gc.setForeground ( getDisplay ().getSystemColor ( SWT.COLOR_INFO_FOREGROUND ) );
            FontData[] fontData = getDisplay ().getSystemFont ().getFontData ();
            for ( FontData fd : fontData )
            {
                fd.setHeight ( 8 );
            }
            Font smallFont = new Font ( gc.getDevice (), fontData );
            gc.setFont ( smallFont );
            String timestampText = "Timestamp: " + DateFormat
                    .getDateTimeInstance ( DateFormat.LONG, DateFormat.LONG )
                        .format ( timestamp );
            String qualityText = "Quality: " + quality;
            Point textSize = gc.textExtent ( timestampText );
            int textWidth = textSize.x;
            int textHeight = textSize.y;
            int height = textHeight * 2 + ( textHeight + padding ) * data.keySet ().size () + padding * 3;
            if ( currentY + height > getPlotArea ().getSize ().y )
            {
                yoffset = -10 - height;
            }
            int width = textWidth + padding * 2;
            if ( currentX + width > getPlotArea ().getSize ().x )
            {
                xoffset = -10 - width;
            }
            gc.fillRoundRectangle ( currentX + xoffset, currentY + yoffset, width, height, corner, corner );
            gc.drawRoundRectangle ( currentX + xoffset, currentY + yoffset, width, height, corner, corner );
            gc.drawText ( timestampText, currentX + xoffset + padding, currentY + yoffset + padding );
            gc.drawText ( qualityText, currentX + xoffset + padding, currentY + yoffset + padding * 2 + textHeight );
            int i = 2;
            for ( Entry<String, Double> entry : data.entrySet () )
            {
                gc
                        .drawText ( entry.getKey () + ": " + entry.getValue (), currentX + xoffset + padding, currentY + yoffset + ( padding + textHeight ) * i + padding );
                i++;
            }
            smallFont.dispose ();
        }
    }

}
