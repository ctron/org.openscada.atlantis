package org.openscada.da.client.chart;

import java.awt.BasicStroke;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
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
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.test.impl.HiveConnection;

public class ChartView extends ViewPart implements ItemUpdateListener
{
    private static Logger _log = Logger.getLogger ( ChartView.class );

    public final static String VIEW_ID = "org.openscada.da.client.chart.ChartView";

    private ChartComposite _frame = null;

    private JFreeChart _chart = null;

    private HiveConnection _connection = null;

    private String _item;

    private TimeSeriesCollection _dataset;

    private TimeSeries _series;

    private RegularTimePeriod _lastTimestamp = null;

    private IntervalMarker _errorMarker = null;

    public ChartView ()
    {

    }

    @Override
    public void createPartControl ( Composite parent )
    {
        try
        {
            _dataset = new TimeSeriesCollection ();

            _series = new TimeSeries ( "Values", FixedMillisecond.class );
            _dataset.addSeries ( _series );

            _chart = ChartFactory.createTimeSeriesChart ( "Data Item Chart", "Time", "Value", _dataset, false, true, false );

            _lastTimestamp = new FixedMillisecond ( Calendar.getInstance ().getTime () );

            _frame = new ChartComposite ( parent, SWT.NONE, _chart, true );
            _frame.pack ();
        }
        catch ( Exception e )
        {
            _log.debug ( "Failed", e );
        }
    }

    @Override
    public void setFocus ()
    {
        if ( _frame != null )
        {
            _frame.setFocus ();
        }
    }

    @Override
    public void dispose ()
    {
        disconnect ();

        if ( _frame != null )
        {
            _frame.dispose ();
        }
        super.dispose ();
    }

    public void setDataItem ( HiveConnection connection, String item )
    {
        disconnect ();
        if ( item == null )
        {
            return;
        }

        connect ( connection, item );
    }

    protected void disconnect ()
    {
        if ( _connection != null )
        {
            _connection.getItemManager ().removeItemUpdateListener ( _item, this );
            _connection = null;
        }
    }

    protected void connect ( HiveConnection connection, String item )
    {
        _item = item;
        _connection = connection;

        _chart.setTitle ( item );

        _connection.getItemManager ().addItemUpdateListener ( _item, this );
    }

    public void notifySubscriptionChange ( SubscriptionState state, Throwable subscriptionError )
    {
    }
    
    public void notifyDataChange ( Variant value, Map<String, Variant> attributes, boolean cache )
    {
        if ( value != null )
        {
            triggerUpdate ( value );
        }
        if ( attributes != null )
        {
            if ( attributes.containsKey ( "error" ) )
            {
                RegularTimePeriod time = new FixedMillisecond ( Calendar.getInstance ().getTime () );
                if ( attributes.get ( "error" ) == null )
                {
                    triggerError ( false, time );
                }
                else
                {
                    triggerError ( attributes.get ( "error" ).asBoolean (), time );
                }
            }

        }
    }

    protected void triggerError ( final boolean state, final RegularTimePeriod time )
    {
        if ( !_frame.getDisplay ().isDisposed () )
        {
            _frame.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !_frame.isDisposed () )
                    {
                        handleError ( state, time );
                        _frame.forceRedraw ();
                    }
                }
            } );
        }
    }

    protected void triggerUpdate ( final Variant value )
    {
        if ( !_frame.getDisplay ().isDisposed () )
        {
            _frame.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !_frame.isDisposed () )
                    {
                        performUpdate ( value );
                        _frame.forceRedraw ();
                    }
                }
            } );
        }

    }

    protected static Number convertToNumber ( Variant value )
    {
        Number n = null;

        try
        {
            n = value.asDouble ();
        }
        catch ( NullValueException e )
        {
        }
        catch ( NotConvertableException e )
        {
        }

        if ( n == null )
        {
            try
            {
                n = value.asLong ();
            }
            catch ( NullValueException e )
            {
            }
            catch ( NotConvertableException e )
            {
            }
        }

        return n;
    }

    protected void performUpdate ( Variant value )
    {
        Number n = convertToNumber ( value );

        RegularTimePeriod time = new FixedMillisecond ( Calendar.getInstance ().getTime () );

        _series.add ( new TimeSeriesDataItem ( time, n ) );

        if ( _errorMarker != null )
        {
            _errorMarker.setEndValue ( time.getLastMillisecond () );
        }

        // update
        _lastTimestamp = time;
        _frame.forceRedraw ();
    }

    protected void handleError ( boolean state, RegularTimePeriod time )
    {
        _log.debug ( String.format ( "Handle error: %s (%s)", state, time ) );

        if ( state )
        {
            if ( _errorMarker != null )
            {
                _errorMarker.setEndValue ( time.getLastMillisecond () );
            }
            else
            {
                _errorMarker = new IntervalMarker ( _lastTimestamp.getFirstMillisecond (), time.getLastMillisecond (), ChartColor.BLUE, new BasicStroke ( 2 ), null, null, 0.5F );
                _chart.getXYPlot ().addDomainMarker ( _errorMarker, Layer.BACKGROUND );
            }
        }
        else
        {
            if ( _errorMarker != null )
            {
                _errorMarker.setEndValue ( time.getLastMillisecond () );
                _errorMarker = null;
            }
        }
        _lastTimestamp = time;
    }
}
