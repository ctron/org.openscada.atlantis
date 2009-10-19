package org.openscada.hd.chart;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private final NumberFormat decimalFormat;

    private final NumberFormat percentFormat;

    private FontData smallFontData;

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
        decimalFormat = DecimalFormat.getNumberInstance ();
        decimalFormat.setGroupingUsed ( true );
        decimalFormat.setMaximumFractionDigits ( 3 );
        decimalFormat.setMinimumFractionDigits ( 3 );
        decimalFormat.setRoundingMode ( RoundingMode.HALF_UP );
        percentFormat = DecimalFormat.getPercentInstance ();

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

        List<FontData> fontDataList = new ArrayList<FontData> ();
        for ( FontData fontData : getDisplay ().getFontList ( "Monaco", true ) )
        {
            fontDataList.add ( fontData );
        }
        for ( FontData fontData : getDisplay ().getFontList ( "Bitstream Vera Sans Mono", true ) )
        {
            fontDataList.add ( fontData );
        }
        for ( FontData fontData : getDisplay ().getFontList ( "Courier New", true ) )
        {
            fontDataList.add ( fontData );
        }
        for ( FontData fontData : getDisplay ().getFontList ( "Courier", true ) )
        {
            fontDataList.add ( fontData );
        }
        FontData[] fontDataDefault = getDisplay ().getSystemFont ().getFontData ();
        if ( fontDataList.size () > 0 )
        {
            smallFontData = fontDataList.get ( 0 );
        }
        else
        {
            smallFontData = fontDataDefault[0];
        }
        smallFontData.setHeight ( fontDataDefault[0].getHeight () - 2 );
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
            Font smallFont = new Font ( gc.getDevice (), smallFontData );
            gc.setFont ( smallFont );
            String timestampText = "     Timestamp: " + DateFormat.getDateTimeInstance ( DateFormat.LONG, DateFormat.LONG ).format ( timestamp );
            String qualityText = "       Quality: " + percentFormat.format ( quality );
            String soureValuesText = "   # of Values: " + dataAtPoint.getSourceValues ( currentX );
            Point textSize = gc.textExtent ( timestampText );
            int textWidth = textSize.x;
            int textHeight = textSize.y;
            int height = textHeight * 4 + ( textHeight + padding ) * data.keySet ().size () + padding * 5;
            if ( currentY + height > getPlotArea ().getSize ().y )
            {
                yoffset = -10 - height;
            }
            int width = textWidth + padding * 3;
            if ( currentX + width > getPlotArea ().getSize ().x )
            {
                xoffset = -10 - width;
            }
            gc.fillRoundRectangle ( currentX + xoffset, currentY + yoffset, width, height, corner, corner );
            gc.drawRoundRectangle ( currentX + xoffset, currentY + yoffset, width, height, corner, corner );
            gc.drawLine ( currentX + xoffset + padding, currentY + yoffset + ( padding + textHeight ) * 4 - padding, currentX + xoffset + width - padding, currentY + yoffset + ( padding + textHeight ) * 4 - padding );
            gc.drawText ( timestampText, currentX + xoffset + padding, currentY + yoffset + padding );
            gc.drawText ( qualityText, currentX + xoffset + padding, currentY + yoffset + padding * 2 + textHeight );
            gc.drawText ( soureValuesText, currentX + xoffset + padding, currentY + yoffset + padding * 3 + textHeight * 2 );
            int i = 4;
            for ( Entry<String, Double> entry : data.entrySet () )
            {
                gc.drawText ( String.format ( "%14s", entry.getKey () ) + ": " + String.format ( "%16s", Double.isNaN ( entry.getValue () ) ? "-" : decimalFormat.format ( entry.getValue () ) ), currentX + xoffset + padding, currentY + yoffset + ( padding + textHeight ) * i + padding );
                i++;
            }
            smallFont.dispose ();
        }
    }

    @Override
    public void dispose ()
    {

        super.dispose ();
    }
}
