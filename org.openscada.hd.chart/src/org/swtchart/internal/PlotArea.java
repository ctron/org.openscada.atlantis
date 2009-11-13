/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.BackgroundOverlay;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeriesSet;
import org.swtchart.internal.series.Series;
import org.swtchart.internal.series.SeriesSet;

/**
 * Plot area to draw series and grids.
 */
public class PlotArea extends Composite implements PaintListener
{

    /** the chart */
    protected Chart chart;

    /** the set of plots */
    protected SeriesSet seriesSet;

    /** the image cache */
    private Image imageCache;

    /** the state indicating if image cache has to be updated */
    private boolean updateImageCache;

    protected BackgroundOverlay backgroundOverlay;

    /** the default background color */
    private static final int DEFAULT_BACKGROUND = SWT.COLOR_WHITE;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     */
    public PlotArea ( final Chart chart, final int style )
    {
        super ( chart, style | SWT.NO_BACKGROUND );

        this.chart = chart;

        seriesSet = new SeriesSet ( chart );
        updateImageCache = true;

        setBackground ( Display.getDefault ().getSystemColor ( DEFAULT_BACKGROUND ) );
        addPaintListener ( this );
    }

    /**
     * Gets the set of series.
     * 
     * @return the set of series
     */
    public ISeriesSet getSeriesSet ()
    {
        return seriesSet;
    }

    /*
     * @see Control#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds ( final int x, final int y, final int width, final int height )
    {
        super.setBounds ( x, y, width, height );
        ( (SeriesSet)getSeriesSet () ).compressAllSeries ();
    }

    /*
     * @see Control#setBackground(Color)
     */
    @Override
    public void setBackground ( final Color color )
    {
        if ( color == null )
        {
            super.setBackground ( Display.getDefault ().getSystemColor ( DEFAULT_BACKGROUND ) );
        }
        else
        {
            super.setBackground ( color );
        }
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl ( final PaintEvent e )
    {
        if ( updateImageCache )
        {
            Point p = getSize ();
            if ( ( imageCache != null ) && !imageCache.isDisposed () )
            {
                imageCache.dispose ();
            }
            imageCache = new Image ( Display.getCurrent (), p.x, p.y );
            GC gc = new GC ( imageCache );

            // draw the plot area background
            gc.setBackground ( getBackground () );
            gc.fillRectangle ( 0, 0, p.x, p.y );

            // draw grid
            for ( IAxis axis : chart.getAxisSet ().getAxes () )
            {
                ( (Grid)axis.getGrid () ).draw ( gc, p.x, p.y );
            }

            // draw additional background overlay
            if ( chart.getBackgroundOverlay () != null )
            {
                chart.getBackgroundOverlay ().draw ( gc, p.x, p.y );
            }

            // draw series. The line series should be drawn on bar series.
            for ( ISeries series : chart.getSeriesSet ().getSeries () )
            {
                if ( series instanceof IBarSeries )
                {
                    ( (Series)series ).draw ( gc, p.x, p.y );
                }
            }
            for ( ISeries series : chart.getSeriesSet ().getSeries () )
            {
                if ( series instanceof ILineSeries )
                {
                    ( (Series)series ).draw ( gc, p.x, p.y );
                }
            }
            gc.dispose ();
            updateImageCache = false;
        }
        e.gc.drawImage ( imageCache, 0, 0 );
    }

    /*
     * @see Control#update()
     */
    @Override
    public void update ()
    {
        super.update ();
        updateImageCache = true;
    }

    /*
     * @see Control#redraw()
     */
    @Override
    public void redraw ()
    {
        super.redraw ();
        updateImageCache = true;
    }

    /*
     * @see Widget#dispose()
     */
    @Override
    public void dispose ()
    {
        super.dispose ();
        seriesSet.dispose ();
        if ( ( imageCache != null ) && !imageCache.isDisposed () )
        {
            imageCache.dispose ();
        }
    }

    public BackgroundOverlay getBackgroundOverlay ()
    {
        return backgroundOverlay;
    }

    public void setBackgroundOverlay ( final BackgroundOverlay backgroundOverlay )
    {
        this.backgroundOverlay = backgroundOverlay;
    }
}
