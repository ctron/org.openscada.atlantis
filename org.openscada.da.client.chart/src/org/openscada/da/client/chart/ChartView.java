package org.openscada.da.client.chart;

import java.awt.BasicStroke;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.base.browser.HiveConnection;
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.client.ItemUpdateListener;

public class ChartView extends ViewPart implements ItemUpdateListener
{
    private static Logger _log = Logger.getLogger ( ChartView.class );

    public final static String VIEW_ID = "org.openscada.da.client.chart.ChartView"; //$NON-NLS-1$

    private ChartComposite _frame = null;

    private JFreeChart _chart = null;

    private HiveConnection _connection = null;

    private DataItemHolder _item;

    private TimeSeriesCollection _dataset;

    private TimeSeries _series;

    private RegularTimePeriod _lastTimestamp = null;

    private IntervalMarker _errorMarker = null;

    public ChartView ()
    {

    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        try
        {
            this._dataset = new TimeSeriesCollection ();

            this._series = new TimeSeries ( Messages.getString("ChartView.seriesLabel.values"), FixedMillisecond.class ); //$NON-NLS-1$
            this._dataset.addSeries ( this._series );

            this._chart = createChart ();

            this._lastTimestamp = new FixedMillisecond ( Calendar.getInstance ().getTime () );

            this._frame = new ChartComposite ( parent, SWT.NONE, this._chart, true );
            this._frame.pack ();
        }
        catch ( final Exception e )
        {
            _log.debug ( "Failed", e ); //$NON-NLS-1$
        }
    }

    private JFreeChart createChart ()
    {
        final ValueAxis timeAxis = new DateAxis ( Messages.getString("ChartView.axis.time") ); //$NON-NLS-1$
        timeAxis.setLowerMargin ( 0.02 ); // reduce the default margins 
        timeAxis.setUpperMargin ( 0.02 );
        final NumberAxis valueAxis = new NumberAxis ( Messages.getString("ChartView.axis.value") ); //$NON-NLS-1$
        valueAxis.setAutoRangeIncludesZero ( false ); // override default
        final XYPlot plot = new XYPlot ( this._dataset, timeAxis, valueAxis, null );

        XYToolTipGenerator toolTipGenerator = null;
        toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance ();

        // final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer ( true, false );
        final XYStepRenderer renderer = new XYStepRenderer ();
        renderer.setBaseToolTipGenerator ( toolTipGenerator );
        plot.setRenderer ( renderer );

        return new JFreeChart ( Messages.getString("ChartView.chartTitle"), JFreeChart.DEFAULT_TITLE_FONT, plot, false ); //$NON-NLS-1$
    }

    @Override
    public void setFocus ()
    {
        if ( this._frame != null )
        {
            this._frame.setFocus ();
        }
    }

    @Override
    public void dispose ()
    {
        disconnect ();

        if ( this._frame != null )
        {
            this._frame.dispose ();
        }
        super.dispose ();
    }

    public void setDataItem ( final DataItemHolder item )
    {
        disconnect ();
        if ( item == null )
        {
            return;
        }

        connect ( item );
    }

    protected void disconnect ()
    {
        if ( this._connection != null )
        {
            this._connection.getItemManager ().removeItemUpdateListener ( this._item.getItemId (), this );
            this._connection = null;
        }
    }

    protected void connect ( final DataItemHolder item )
    {
        this._item = item;

        this._chart.setTitle ( item.getItemId () );

        this._connection.getItemManager ().addItemUpdateListener ( this._item.getItemId (), this );
    }

    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable subscriptionError )
    {
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        if ( value != null )
        {
            triggerUpdate ( value );
        }
        if ( attributes != null )
        {
            if ( attributes.containsKey ( "error" ) ) //$NON-NLS-1$
            {
                final RegularTimePeriod time = new FixedMillisecond ( Calendar.getInstance ().getTime () );
                if ( attributes.get ( "error" ) == null ) //$NON-NLS-1$
                {
                    triggerError ( false, time );
                }
                else
                {
                    triggerError ( attributes.get ( "error" ).asBoolean (), time ); //$NON-NLS-1$
                }
            }

        }
    }

    protected void triggerError ( final boolean state, final RegularTimePeriod time )
    {
        if ( !this._frame.getDisplay ().isDisposed () )
        {
            this._frame.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !ChartView.this._frame.isDisposed () )
                    {
                        handleError ( state, time );
                        ChartView.this._frame.forceRedraw ();
                    }
                }
            } );
        }
    }

    protected void triggerUpdate ( final Variant value )
    {
        if ( !this._frame.getDisplay ().isDisposed () )
        {
            this._frame.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !ChartView.this._frame.isDisposed () )
                    {
                        performUpdate ( value );
                        ChartView.this._frame.forceRedraw ();
                    }
                }
            } );
        }

    }

    protected static Number convertToNumber ( final Variant value )
    {
        Number n = null;

        try
        {
            n = value.asDouble ();
        }
        catch ( final NullValueException e )
        {
        }
        catch ( final NotConvertableException e )
        {
        }

        if ( n == null )
        {
            try
            {
                n = value.asLong ();
            }
            catch ( final NullValueException e )
            {
            }
            catch ( final NotConvertableException e )
            {
            }
        }

        return n;
    }

    protected void performUpdate ( final Variant value )
    {
        final Number n = convertToNumber ( value );

        final RegularTimePeriod time = new FixedMillisecond ( Calendar.getInstance ().getTime () );

        this._series.add ( new TimeSeriesDataItem ( time, n ) );

        if ( this._errorMarker != null )
        {
            this._errorMarker.setEndValue ( time.getLastMillisecond () );
        }

        // update
        this._lastTimestamp = time;
        this._frame.forceRedraw ();
    }

    protected void handleError ( final boolean state, final RegularTimePeriod time )
    {
        _log.debug ( String.format ( "Handle error: %s (%s)", state, time ) ); //$NON-NLS-1$

        if ( state )
        {
            if ( this._errorMarker != null )
            {
                this._errorMarker.setEndValue ( time.getLastMillisecond () );
            }
            else
            {
                this._errorMarker = new IntervalMarker ( this._lastTimestamp.getFirstMillisecond (), time.getLastMillisecond (), ChartColor.BLUE, new BasicStroke ( 2 ), null, null, 0.5F );
                this._chart.getXYPlot ().addDomainMarker ( this._errorMarker, Layer.BACKGROUND );
            }
        }
        else
        {
            if ( this._errorMarker != null )
            {
                this._errorMarker.setEndValue ( time.getLastMillisecond () );
                this._errorMarker = null;
            }
        }
        this._lastTimestamp = time;
    }
}
