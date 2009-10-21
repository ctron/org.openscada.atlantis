/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.series;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IDisposeListener;
import org.swtchart.IErrorBar;
import org.swtchart.ISeries;
import org.swtchart.ISeriesLabel;
import org.swtchart.Range;
import org.swtchart.IAxis.Direction;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.ICompress;

/**
 * Series.
 */
abstract public class Series implements ISeries
{

    /** the default series type */
    protected static final SeriesType DEFAULT_SERIES_TYPE = SeriesType.LINE;

    /** the x series */
    protected double[] xSeries;

    /** the y series */
    protected double[] ySeries;

    /** the minimum value of x series */
    protected double minX;

    /** the maximum value of x series */
    protected double maxX;

    /** the minimum value of y series */
    protected double minY;

    /** the maximum value of y series */
    protected double maxY;

    /** the series id */
    protected String id;

    /** the compressor */
    protected ICompress compressor;

    /** the x axis id */
    protected int xAxisId;

    /** the y axis id */
    protected int yAxisId;

    /** the visibility of series */
    protected boolean visible;

    /** the state indicating whether x series are monotone increasing */
    protected boolean isXMonotoneIncreasing;

    /** the series type */
    protected SeriesType type;

    /** the series label */
    protected SeriesLabel seriesLabel;

    /** the x error bar */
    protected ErrorBar xErrorBar;

    /** the y error bar */
    protected ErrorBar yErrorBar;

    /** the chart */
    protected Chart chart;

    /** the state indicating if the series is a stacked type */
    protected boolean stackEnabled;

    /** the stack series */
    protected double[] stackSeries;

    /** the state indicating if the type of X series is <tt>Date</tt> */
    private boolean isDateSeries;

    /** the list of dispose listeners */
    private final List<IDisposeListener> listeners;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param id
     *            the series id
     */
    protected Series ( final Chart chart, final String id )
    {
        super ();

        this.chart = chart;
        this.id = id;
        this.xAxisId = 0;
        this.yAxisId = 0;
        this.visible = true;
        this.type = DEFAULT_SERIES_TYPE;
        this.stackEnabled = false;
        this.isXMonotoneIncreasing = true;
        this.seriesLabel = new SeriesLabel ();
        this.xErrorBar = new ErrorBar ();
        this.yErrorBar = new ErrorBar ();
        this.listeners = new ArrayList<IDisposeListener> ();
    }

    /*
     * @see ISeries#getId()
     */
    public String getId ()
    {
        return this.id;
    }

    /*
     * @see ISeries#setVisible(boolean)
     */
    public void setVisible ( final boolean visible )
    {
        if ( this.visible == visible )
        {
            return;
        }

        this.visible = visible;

        ( (SeriesSet)this.chart.getSeriesSet () ).updateStackAndRiserData ();
    }

    /*
     * @see ISeries#isVisible()
     */
    public boolean isVisible ()
    {
        return this.visible;
    }

    /*
     * @see ISeries#getType()
     */
    public SeriesType getType ()
    {
        return this.type;
    }

    /*
     * @see ISeries#isStackEnabled()
     */
    public boolean isStackEnabled ()
    {
        return this.stackEnabled;
    }

    /*
     * @see ISeries#enableStack(boolean)
     */
    public void enableStack ( final boolean enabled )
    {
        if ( enabled && this.minY < 0 )
        {
            throw new IllegalStateException ( "Stacked series cannot contain minus values." );
        }

        if ( this.stackEnabled == enabled )
        {
            return;
        }

        this.stackEnabled = enabled;

        ( (SeriesSet)this.chart.getSeriesSet () ).updateStackAndRiserData ();
    }

    /*
     * @see ISeries#setXSeries(double[])
     */
    public void setXSeries ( final double[] series )
    {

        if ( series == null )
        {
            SWT.error ( SWT.ERROR_NULL_ARGUMENT );
            return; // to suppress warning...
        }

        this.xSeries = new double[series.length];
        System.arraycopy ( series, 0, this.xSeries, 0, series.length );
        this.isDateSeries = false;

        if ( this.xSeries.length == 0 )
        {
            return;
        }

        // find the min and max value of x series
        this.minX = this.xSeries[0];
        this.maxX = this.xSeries[0];
        for ( int i = 1; i < this.xSeries.length; i++ )
        {
            if ( this.minX > this.xSeries[i] )
            {
                this.minX = this.xSeries[i];
            }
            if ( this.maxX < this.xSeries[i] )
            {
                this.maxX = this.xSeries[i];
            }

            if ( this.xSeries[i - 1] > this.xSeries[i] )
            {
                this.isXMonotoneIncreasing = false;
            }
        }

        setCompressor ();

        this.compressor.setXSeries ( this.xSeries );
        if ( this.ySeries != null )
        {
            this.compressor.setYSeries ( this.ySeries );
        }

        if ( this.minX <= 0 )
        {
            final IAxis axis = this.chart.getAxisSet ().getXAxis ( this.xAxisId );
            if ( axis != null )
            {
                axis.enableLogScale ( false );
            }
        }
    }

    /*
     * @see ISeries#getXSeries()
     */
    public double[] getXSeries ()
    {
        if ( this.xSeries == null )
        {
            return null;
        }

        final double[] copiedSeries = new double[this.xSeries.length];
        System.arraycopy ( this.xSeries, 0, copiedSeries, 0, this.xSeries.length );

        return copiedSeries;
    }

    /*
     * @see ISeries#setYSeries(double[])
     */
    public void setYSeries ( final double[] series )
    {

        if ( series == null )
        {
            SWT.error ( SWT.ERROR_NULL_ARGUMENT );
            return; // to suppress warning...
        }

        this.ySeries = new double[series.length];
        System.arraycopy ( series, 0, this.ySeries, 0, series.length );

        if ( this.ySeries.length == 0 )
        {
            return;
        }

        // find the min and max value of y series
        this.minY = this.ySeries[0];
        this.maxY = this.ySeries[0];
        for ( int i = 1; i < this.ySeries.length; i++ )
        {
            if ( this.minY > this.ySeries[i] )
            {
                this.minY = this.ySeries[i];
            }
            if ( this.maxY < this.ySeries[i] )
            {
                this.maxY = this.ySeries[i];
            }
        }

        if ( this.xSeries == null || this.xSeries.length != series.length )
        {
            this.xSeries = new double[series.length];
            for ( int i = 0; i < series.length; i++ )
            {
                this.xSeries[i] = i;
            }
            this.minX = this.xSeries[0];
            this.maxX = this.xSeries[this.xSeries.length - 1];
            this.isXMonotoneIncreasing = true;
        }

        setCompressor ();

        this.compressor.setXSeries ( this.xSeries );
        this.compressor.setYSeries ( this.ySeries );

        if ( this.minX <= 0 )
        {
            final IAxis axis = this.chart.getAxisSet ().getXAxis ( this.xAxisId );
            if ( axis != null )
            {
                axis.enableLogScale ( false );
            }
        }
        if ( this.minY <= 0 )
        {
            final IAxis axis = this.chart.getAxisSet ().getYAxis ( this.yAxisId );
            if ( axis != null )
            {
                axis.enableLogScale ( false );
            }
            this.stackEnabled = false;
        }
    }

    /*
     * @see ISeries#getYSeries()
     */
    public double[] getYSeries ()
    {
        if ( this.ySeries == null )
        {
            return null;
        }

        final double[] copiedSeries = new double[this.ySeries.length];
        System.arraycopy ( this.ySeries, 0, copiedSeries, 0, this.ySeries.length );

        return copiedSeries;
    }

    /*
     * @see ISeries#setXDateSeries(Date[])
     */
    public void setXDateSeries ( final Date[] series )
    {
        if ( series == null )
        {
            SWT.error ( SWT.ERROR_NULL_ARGUMENT );
            return; // to suppress warning...
        }

        final double[] xDateSeries = new double[series.length];
        for ( int i = 0; i < series.length; i++ )
        {
            if ( series[i] != null )
            {
                xDateSeries[i] = series[i].getTime ();
            }
            else
            {
                xDateSeries[i] = Double.NaN;
            }
        }
        setXSeries ( xDateSeries );
        this.isDateSeries = true;
    }

    /*
     * @see ISeries#getXDateSeries()
     */
    public Date[] getXDateSeries ()
    {
        if ( !this.isDateSeries )
        {
            return null;
        }

        final Date[] series = new Date[this.xSeries.length];
        for ( int i = 0; i < series.length; i++ )
        {
            series[i] = new Date ( (long)this.xSeries[i] );
        }
        return series;
    }

    /**
     * Gets the state indicating if date series is set.
     * 
     * @return true if date series is set
     */
    public boolean isDateSeries ()
    {
        return this.isDateSeries;
    }

    /**
     * Gets the state indicating if the series is valid stack series.
     * 
     * @return true if the series is valid stack series
     */
    public boolean isValidStackSeries ()
    {
        return this.stackEnabled && this.stackSeries != null && this.stackSeries.length > 0 && !this.chart.getAxisSet ().getYAxis ( this.yAxisId ).isLogScaleEnabled () && ( (Axis)this.chart.getAxisSet ().getXAxis ( this.xAxisId ) ).isValidCategoryAxis ();
    }

    /**
     * Gets the X range of series.
     * 
     * @return the X range of series
     */
    public Range getXRange ()
    {
        double min = this.minX;
        double max = this.maxX;
        if ( min == max )
        {
            min = min - 0.5;
            max = max + 0.5;
        }
        return new Range ( min, max );
    }

    /**
     * Gets the adjusted range to show all series in screen. This range includes
     * the size of plot like symbol or bar.
     * 
     * @param axis
     *            the axis
     * @param length
     *            the axis length in pixels
     * @return the adjusted range
     */
    abstract public Range getAdjustedRange ( Axis axis, int length );

    /**
     * Gets the Y range of series.
     * 
     * @return the Y range of series
     */
    public Range getYRange ()
    {
        final double min = this.minY;
        double max = this.maxY;
        final Axis xAxis = (Axis)this.chart.getAxisSet ().getXAxis ( this.xAxisId );
        if ( isValidStackSeries () && xAxis.isValidCategoryAxis () )
        {
            for ( int i = 0; i < this.stackSeries.length; i++ )
            {
                if ( max < this.stackSeries[i] )
                {
                    max = this.stackSeries[i];
                }
            }
        }
        return new Range ( min, max );
    }

    /**
     * Gets the compressor.
     * 
     * @return the compressor
     */
    protected ICompress getCompressor ()
    {
        return this.compressor;
    }

    /**
     * Sets the compressor.
     */
    abstract protected void setCompressor ();

    /*
     * @see ISeries#getXAxisId()
     */
    public int getXAxisId ()
    {
        return this.xAxisId;
    }

    /*
     * @see ISeries#setXAxisId(int)
     */
    public void setXAxisId ( final int id )
    {
        if ( this.xAxisId == id )
        {
            return;
        }

        final IAxis axis = this.chart.getAxisSet ().getXAxis ( this.xAxisId );

        if ( this.minX <= 0 && axis != null && axis.isLogScaleEnabled () )
        {
            this.chart.getAxisSet ().getXAxis ( this.xAxisId ).enableLogScale ( false );
        }

        this.xAxisId = id;

        ( (SeriesSet)this.chart.getSeriesSet () ).updateStackAndRiserData ();
    }

    /*
     * @see ISeries#getYAxisId()
     */
    public int getYAxisId ()
    {
        return this.yAxisId;
    }

    /*
     * @see ISeries#setYAxisId(int)
     */
    public void setYAxisId ( final int id )
    {
        this.yAxisId = id;
    }

    /*
     * @see ISeries#getLabel()
     */
    public ISeriesLabel getLabel ()
    {
        return this.seriesLabel;
    }

    /*
     * @see ISeries#getXErrorBar()
     */
    public IErrorBar getXErrorBar ()
    {
        return this.xErrorBar;
    }

    /*
     * @see ISeries#getYErrorBar()
     */
    public IErrorBar getYErrorBar ()
    {
        return this.yErrorBar;
    }

    /**
     * Sets the stack series
     * 
     * @param stackSeries
     */
    protected void setStackSeries ( final double[] stackSeries )
    {
        this.stackSeries = stackSeries;
    }

    /*
     * @see ISeries#getPixelCoordinates(int)
     */
    public Point getPixelCoordinates ( final int index )
    {

        // get the horizontal and vertical axes
        IAxis hAxis;
        IAxis vAxis;
        if ( this.chart.getOrientation () == SWT.HORIZONTAL )
        {
            hAxis = this.chart.getAxisSet ().getXAxis ( this.xAxisId );
            vAxis = this.chart.getAxisSet ().getYAxis ( this.yAxisId );
        }
        else if ( this.chart.getOrientation () == SWT.VERTICAL )
        {
            hAxis = this.chart.getAxisSet ().getYAxis ( this.yAxisId );
            vAxis = this.chart.getAxisSet ().getXAxis ( this.xAxisId );
        }
        else
        {
            throw new IllegalStateException ( "unknown chart orientation" ); //$NON-NLS-1$
        }

        // get the pixel coordinates
        return new Point ( getPixelCoordinate ( hAxis, index ), getPixelCoordinate ( vAxis, index ) );
    }

    /**
     * Gets the pixel coordinates with given axis and series index.
     * 
     * @param axis
     *            the axis
     * @param index
     *            the series index
     * @return the pixel coordinates
     */
    private int getPixelCoordinate ( final IAxis axis, final int index )
    {

        // get the data coordinate
        double dataCoordinate;
        if ( axis.getDirection () == Direction.X )
        {
            if ( axis.isCategoryEnabled () )
            {
                dataCoordinate = index;
            }
            else
            {
                if ( index < 0 || this.xSeries.length <= index )
                {
                    throw new IllegalArgumentException ( "Series index is out of range." ); //$NON-NLS-1$
                }
                dataCoordinate = this.xSeries[index];
            }
        }
        else if ( axis.getDirection () == Direction.Y )
        {
            if ( isValidStackSeries () )
            {
                if ( index < 0 || this.stackSeries.length <= index )
                {
                    throw new IllegalArgumentException ( "Series index is out of range." ); //$NON-NLS-1$
                }
                dataCoordinate = this.stackSeries[index];
            }
            else
            {
                if ( index < 0 || this.ySeries.length <= index )
                {
                    throw new IllegalArgumentException ( "Series index is out of range." ); //$NON-NLS-1$
                }
                dataCoordinate = this.ySeries[index];
            }
        }
        else
        {
            throw new IllegalStateException ( "unknown axis direction" ); //$NON-NLS-1$
        }

        // get the pixel coordinate
        return axis.getPixelCoordinate ( dataCoordinate );
    }

    /**
     * Gets the range with given margin.
     * 
     * @param lowerPlotMargin
     *            the lower margin in pixels
     * @param upperPlotMargin
     *            the upper margin in pixels
     * @param length
     *            the axis length in pixels
     * @param axis
     *            the axis
     * @param range
     *            the range
     * @return the range with margin
     */
    protected Range getRangeWithMargin ( final int lowerPlotMargin, final int upperPlotMargin, final int length, final Axis axis, final Range range )
    {
        if ( length == 0 )
        {
            return range;
        }

        final int lowerPixelCoordinate = axis.getPixelCoordinate ( range.lower, range.lower, range.upper ) + lowerPlotMargin * ( axis.isHorizontalAxis () ? -1 : 1 );
        final int upperPixelCoordinate = axis.getPixelCoordinate ( range.upper, range.lower, range.upper ) + upperPlotMargin * ( axis.isHorizontalAxis () ? 1 : -1 );

        final double lower = axis.getDataCoordinate ( lowerPixelCoordinate, range.lower, range.upper );
        final double upper = axis.getDataCoordinate ( upperPixelCoordinate, range.lower, range.upper );

        return new Range ( lower, upper );
    }

    /**
     * Disposes SWT resources.
     */
    protected void dispose ()
    {
        for ( final IDisposeListener listener : this.listeners )
        {
            listener.disposed ( new Event () );
        }
    }

    /*
     * @see IAxis#addDisposeListener(IDisposeListener)
     */
    public void addDisposeListener ( final IDisposeListener listener )
    {
        this.listeners.add ( listener );
    }

    /**
     * Draws series.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     */
    public void draw ( final GC gc, final int width, final int height )
    {

        if ( !this.visible || width < 0 || height < 0 || this.xSeries == null || this.xSeries.length == 0 || this.ySeries == null || this.ySeries.length == 0 )
        {
            return;
        }

        final Axis xAxis = (Axis)this.chart.getAxisSet ().getXAxis ( getXAxisId () );
        final Axis yAxis = (Axis)this.chart.getAxisSet ().getYAxis ( getYAxisId () );
        if ( xAxis == null || yAxis == null )
        {
            return;
        }

        draw ( gc, width, height, xAxis, yAxis );
    }

    /**
     * Draws series.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     * @param xAxis
     *            the x axis
     * @param yAxis
     *            the y axis
     */
    abstract protected void draw ( GC gc, int width, int height, Axis xAxis, Axis yAxis );
}
