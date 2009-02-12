package org.openscada.da.client.chart.view;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
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
import org.openscada.core.ConnectionInformation;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.base.connection.ConnectionManager;
import org.openscada.da.base.connection.ConnectionManagerEntry;
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.client.AsyncDataItem;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.chart.Messages;

public class ChartView2 extends ViewPart implements Observer
{
    private static Logger log = Logger.getLogger ( ChartView2.class );

    public final static String VIEW_ID = "org.openscada.da.client.chart.ChartView"; //$NON-NLS-1$

    private static final int REFRESH_DELAY = 1000;

    private ChartComposite frame = null;

    private JFreeChart chart = null;

    private DataItemHolder item;

    private TimeSeriesCollection dataset;

    private TimeSeries series;

    private DataItem dataItem;

    private Display display;

    public ChartView2 ()
    {

    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        try
        {
            this.display = parent.getDisplay ();

            this.dataset = new TimeSeriesCollection ();

            this.series = new TimeSeries ( Messages.getString ( "ChartView.seriesLabel.values" ), FixedMillisecond.class ); //$NON-NLS-1$
            this.dataset.addSeries ( this.series );

            this.chart = createChart ();

            this.frame = new ChartComposite ( parent, SWT.NONE, this.chart, true );
            this.frame.pack ();

            if ( this.item != null )
            {
                this.chart.setTitle ( this.item.getItemId () );
            }

            scheduleUpdate ();
        }
        catch ( final Throwable e )
        {
            log.debug ( "Failed", e ); //$NON-NLS-1$
        }
    }

    private void scheduleUpdate ()
    {
        if ( !this.display.isDisposed () )
        {
            this.display.timerExec ( REFRESH_DELAY, new Runnable () {

                public void run ()
                {
                    triggerUpdate ();
                    scheduleUpdate ();
                }
            } );
        }
    }

    private JFreeChart createChart ()
    {
        final ValueAxis timeAxis = new DateAxis ( Messages.getString ( "ChartView.axis.time" ) ); //$NON-NLS-1$
        timeAxis.setLowerMargin ( 0.02 ); // reduce the default margins 
        timeAxis.setUpperMargin ( 0.02 );
        final NumberAxis valueAxis = new NumberAxis ( Messages.getString ( "ChartView.axis.value" ) ); //$NON-NLS-1$
        valueAxis.setAutoRangeIncludesZero ( false ); // override default
        final XYPlot plot = new XYPlot ( this.dataset, timeAxis, valueAxis, null );

        XYToolTipGenerator toolTipGenerator = null;
        toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance ();

        // final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer ( true, false );
        final XYStepRenderer renderer = new XYStepRenderer ();
        renderer.setBaseToolTipGenerator ( toolTipGenerator );
        plot.setRenderer ( renderer );

        return new JFreeChart ( Messages.getString ( "ChartView.chartTitle" ), JFreeChart.DEFAULT_TITLE_FONT, plot, false ); //$NON-NLS-1$
    }

    @Override
    public void setFocus ()
    {
        if ( this.frame != null )
        {
            this.frame.setFocus ();
        }
    }

    @Override
    public void dispose ()
    {
        disconnect ();

        if ( this.frame != null )
        {
            this.frame.dispose ();
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
        if ( this.item != null )
        {
            this.item = null;
        }
        if ( this.dataItem != null )
        {
            this.dataItem.deleteObserver ( this );

            this.dataItem.unregister ();
            this.dataItem = null;
        }
    }

    protected void connect ( final DataItemHolder item )
    {
        this.item = item;
        this.dataItem = new AsyncDataItem ( item.getItemId (), this.item.getItemManager () );

        this.dataItem.addObserver ( this );

        if ( this.chart != null )
        {
            this.chart.setTitle ( item.getItemId () );
        }
    }

    protected void triggerUpdate ()
    {
        if ( !this.display.isDisposed () )
        {
            this.frame.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !ChartView2.this.frame.isDisposed () )
                    {
                        performUpdate ();
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

    @Override
    public void init ( final IViewSite site, final IMemento memento ) throws PartInitException
    {
        if ( memento != null )
        {
            final String itemId = memento.getString ( "itemId" );
            final String connectionUri = memento.getString ( "connectionUri" );

            if ( itemId != null && connectionUri != null )
            {
                final ConnectionManagerEntry entry = ConnectionManager.getDefault ().getEntry ( ConnectionInformation.fromURI ( connectionUri ), false );
                setDataItem ( new DataItemHolder ( entry.getConnection (), entry.getItemManager (), itemId ) );
            }
        }

        super.init ( site, memento );
    }

    @Override
    public void saveState ( final IMemento memento )
    {
        if ( this.item != null )
        {
            final String itemId = this.item.getItemId ();
            final String connectionUri = this.item.getConnection ().getConnectionInformation ().toString ();

            memento.putString ( "itemId", itemId );
            memento.putString ( "connectionUri", connectionUri );
        }

        super.saveState ( memento );
    }

    protected void performUpdate ()
    {
        final Number n = convertToNumber ( this.dataItem.getValue () );

        final RegularTimePeriod time = new FixedMillisecond ( Calendar.getInstance ().getTime () );

        final TimeSeriesDataItem di = new TimeSeriesDataItem ( time, n );

        final long end = this.series.getMaximumItemAge ();
        final long now = time.getLastMillisecond ();

        this.series.add ( di );

        this.chart.getXYPlot ().addDomainMarker ( new IntervalMarker ( end, now ) );

        // update
        this.frame.forceRedraw ();
    }

    public void update ( final Observable o, final Object arg )
    {
        triggerUpdate ();
    }

}
