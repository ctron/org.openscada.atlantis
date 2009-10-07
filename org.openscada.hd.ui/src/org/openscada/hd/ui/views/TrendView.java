package org.openscada.hd.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.ui.data.QueryBufferBean;
import org.swtchart.Chart;
import org.swtchart.IAxisTick;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

public class TrendView extends ViewPart implements QueryListener
{
    /**
     * @author jrose
     * 
     * holds a range of two dates (from - two), is used as a return value for zooming functionality
     */
    public static class DateRange
    {
        private final Date start;

        private final Date end;

        public DateRange ( final Date start, final Date end )
        {
            this.start = start;
            this.end = end;
        }

        public Date getStart ()
        {
            return start;
        }

        public Date getEnd ()
        {
            return end;
        }
    }

    /**
     * Immutable
     * 
     * @author jrose
     */
    public static class ChartParameters implements Cloneable
    {
        private static final long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

        private int quality = 75;

        private int numOfEntries = 255;

        private Date startTime = null;

        private Date endTime = null;

        private final List<String> availableSeries = new ArrayList<String> ();

        public static class ChartParameterBuilder
        {
            private final ChartParameters parameters;

            private ChartParameterBuilder ()
            {
                parameters = new ChartParameters ();
                long t = System.currentTimeMillis ();
                parameters.startTime = new Date ( t - DAY_IN_MILLISECONDS );
                parameters.endTime = new Date ( t );
            }

            public ChartParameterBuilder from ( final ChartParameters parameters )
            {
                this.quality ( parameters.quality );
                this.numOfEntries ( parameters.numOfEntries );
                this.startTime ( parameters.startTime );
                this.endTime ( parameters.endTime );
                this.availableSeries ( parameters.availableSeries );
                return this;
            }

            public ChartParameterBuilder quality ( final int quality )
            {
                parameters.quality = quality;
                return this;
            }

            public ChartParameterBuilder numOfEntries ( final int numOfEntries )
            {
                parameters.numOfEntries = numOfEntries;
                return this;
            }

            public ChartParameterBuilder startTime ( final Date startTime )
            {
                parameters.startTime = ( startTime == null ? null : (Date)startTime.clone () );
                return this;
            }

            public ChartParameterBuilder endTime ( final Date endTime )
            {
                parameters.endTime = ( endTime == null ? null : (Date)endTime.clone () );
                return this;
            }

            public ChartParameterBuilder availableSeries ( final Iterable<String> availableSeries )
            {
                parameters.availableSeries.clear ();
                for ( String series : availableSeries )
                {
                    parameters.availableSeries.add ( series );
                }
                return this;
            }

            public ChartParameterBuilder availableSeries ( final String[] availableSeries )
            {
                parameters.availableSeries.clear ();
                for ( String series : availableSeries )
                {
                    parameters.availableSeries.add ( series );
                }
                return this;
            }

            public ChartParameters construct ()
            {
                return parameters;
            }
        }

        private ChartParameters ()
        {
        }

        public static ChartParameterBuilder create ()
        {
            return new ChartParameterBuilder ();
        }

        public int getQuality ()
        {
            return quality;
        }

        public int getNumOfEntries ()
        {
            return numOfEntries;
        }

        public Date getStartTime ()
        {
            return startTime == null ? null : (Date)startTime.clone ();
        }

        public Date getEndTime ()
        {
            return endTime == null ? null : (Date)endTime.clone ();
        }

        public List<String> getAvailableSeries ()
        {
            return Collections.unmodifiableList ( availableSeries );
        }

        @Override
        protected Object clone () throws CloneNotSupportedException
        {
            ChartParameters parameters = new ChartParameters ();
            parameters.quality = getQuality ();
            parameters.numOfEntries = getNumOfEntries ();
            parameters.startTime = getStartTime ();
            parameters.endTime = getEndTime ();
            parameters.availableSeries.addAll ( getAvailableSeries () );
            return parameters;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( endTime == null ) ? 0 : endTime.hashCode () );
            result = prime * result + numOfEntries;
            result = prime * result + quality;
            result = prime * result + ( ( startTime == null ) ? 0 : startTime.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            ChartParameters other = (ChartParameters)obj;
            if ( endTime == null )
            {
                if ( other.endTime != null )
                {
                    return false;
                }
            }
            else if ( !endTime.equals ( other.endTime ) )
            {
                return false;
            }
            if ( numOfEntries != other.numOfEntries )
            {
                return false;
            }
            if ( quality != other.quality )
            {
                return false;
            }
            if ( startTime == null )
            {
                if ( other.startTime != null )
                {
                    return false;
                }
            }
            else if ( !startTime.equals ( other.startTime ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public String toString ()
        {
            String result = "ChartParameters = {";
            result += "quality: " + quality;
            result += ", numOfEntries: " + numOfEntries;
            result += ", startTime: " + startTime;
            result += ", endTime: " + endTime;
            return result + " }";
        }
    }

    // internal
    private final static long GUI_JOB_DELAY = 150;

    private final static long GUI_RESIZE_JOB_DELAY = 1500;

    private static final String SMALL_LABEL_FONT = "small-label-font";

    private final AtomicReference<Job> parameterUpdateJob = new AtomicReference<Job> ();

    private final AtomicReference<Job> rangeUpdateJob = new AtomicReference<Job> ();

    private final AtomicReference<Job> dataUpdateJob = new AtomicReference<Job> ();

    // data (model)
    private final AtomicReference<QueryBufferBean> query = new AtomicReference<QueryBufferBean> ();

    private final ConcurrentMap<String, double[]> data = new ConcurrentHashMap<String, double[]> ();

    private Date[] dataLabel = null;

    private final AtomicReference<ChartParameters> chartParameters = new AtomicReference<ChartParameters> ();

    // gui
    private Composite parent;

    private Composite panel;

    private Group qualityGroup;

    private RowLayout groupLayout;

    private Spinner qualitySpinner;

    private Button qualityColorButton;

    private Chart chart;

    private final ConcurrentMap<String, Group> seriesGroups = new ConcurrentHashMap<String, Group> ();

    private Cursor dragCursor;

    private volatile boolean dragStarted = false;

    private volatile int dragStartedX = -1;

    private volatile int dragStartedY = -1;

    private FontRegistry fontRegistry;

    private ColorRegistry colorRegistry;

    @Override
    public void createPartControl ( final Composite parent )
    {
        // chart has some predefined parameters, quality of 0.75, from yesterday to today
        chartParameters.set ( ChartParameters.create ().construct () );

        this.parent = parent;

        // layout for composite
        GridLayout layout = new GridLayout ();
        parent.setLayout ( layout );

        // create panel to contain items for chart control
        panel = new Composite ( parent, SWT.NONE );
        panel.setLayoutData ( new GridData ( SWT.CENTER, SWT.BEGINNING, true, false ) );
        RowLayout panelLayout = new RowLayout ( SWT.HORIZONTAL );
        panelLayout.center = true;
        panel.setLayout ( panelLayout );

        // add label for Spinner
        qualityGroup = new Group ( panel, SWT.SHADOW_ETCHED_IN );

        groupLayout = new RowLayout ( SWT.HORIZONTAL );
        groupLayout.center = true;
        qualityGroup.setLayout ( groupLayout );
        qualityGroup.setText ( "Quality:" );

        // add spinner
        qualitySpinner = new Spinner ( qualityGroup, SWT.BORDER );
        qualitySpinner.setDigits ( 2 );
        qualitySpinner.setMaximum ( 100 );
        qualitySpinner.setMinimum ( 0 );
        qualitySpinner.setSelection ( chartParameters.get ().getQuality () );
        qualitySpinner.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                ChartParameters newParameters = ChartParameters
                        .create ()
                            .from ( chartParameters.get () )
                            .quality ( qualitySpinner.getSelection () )
                            .construct ();
                chartParameters.set ( newParameters );
                parameterUpdateJob.get ().schedule ( GUI_JOB_DELAY );
            }
        } );

        colorRegistry = new ColorRegistry ( parent.getDisplay () );
        colorRegistry.put ( "quality", new RGB ( 255, 128, 128 ) );
        qualityColorButton = new Button ( qualityGroup, SWT.PUSH );
        qualityColorButton.setText ( "Color" );
        qualityColorButton.setBackground ( colorRegistry.get ( "quality" ) );
        qualityColorButton.addSelectionListener ( new SelectionListener () {
            public void widgetSelected ( final SelectionEvent e )
            {
                ColorDialog cd = new ColorDialog ( parent.getShell () );
                cd.setText ( "Select color ..." );
                RGB resultColor = cd.open ();
                if ( resultColor != null )
                {
                    colorRegistry.put ( "quality", resultColor );
                    qualityColorButton.setBackground ( colorRegistry.get ( "quality" ) );
                }
            }

            public void widgetDefaultSelected ( final SelectionEvent e )
            {
            }
        } );

        // font for chart labels
        FontData[] smallFont = JFaceResources.getDefaultFontDescriptor ().getFontData ();
        smallFont[0].height = smallFont[0].height - 2;
        fontRegistry = new FontRegistry ( parent.getDisplay () );
        fontRegistry.put ( SMALL_LABEL_FONT, smallFont );

        // add chart
        chart = new Chart ( parent, SWT.NONE );
        chart.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, true ) );
        chart.getTitle ().setText ( "no item selected" );
        chart.getTitle ().setForeground ( parent.getDisplay ().getSystemColor ( SWT.COLOR_WIDGET_FOREGROUND ) );
        chart.getTitle ().setFont ( JFaceResources.getHeaderFont () );
        chart.getLegend ().setPosition ( SWT.BOTTOM );
        chart.getAxisSet ().getXAxis ( 0 ).getTitle ().setVisible ( false );
        chart.getAxisSet ().getXAxis ( 0 ).getTick ().setForeground ( parent
                .getDisplay ()
                    .getSystemColor ( SWT.COLOR_WIDGET_FOREGROUND ) );
        chart.getAxisSet ().getXAxis ( 0 ).getTick ().setFont ( fontRegistry.get ( SMALL_LABEL_FONT ) );
        chart.getAxisSet ().getXAxis ( 0 ).getGrid ().setStyle ( LineStyle.NONE );
        chart.getAxisSet ().getYAxis ( 0 ).getTitle ().setVisible ( false );
        chart.getAxisSet ().getYAxis ( 0 ).getTick ().setForeground ( parent
                .getDisplay ()
                    .getSystemColor ( SWT.COLOR_WIDGET_FOREGROUND ) );
        chart.getAxisSet ().getYAxis ( 0 ).getTick ().setFont ( fontRegistry.get ( SMALL_LABEL_FONT ) );
        chart.getAxisSet ().getYAxis ( 0 ).getGrid ().setStyle ( LineStyle.NONE );
        // if size of plot has changed, a new request should be made to account
        // for changed numbers of displayed entries
        chart.getPlotArea ().addControlListener ( new ControlListener () {
            public void controlResized ( final ControlEvent e )
            {
                ChartParameters newParameters = ChartParameters
                        .create ()
                            .from ( chartParameters.get () )
                            .numOfEntries ( chart.getPlotArea ().getBounds ().width )
                            .construct ();
                chartParameters.set ( newParameters );
                rangeUpdateJob.get ().schedule ( GUI_RESIZE_JOB_DELAY );
            }

            public void controlMoved ( final ControlEvent e )
            {
            }
        } );
        chart.getPlotArea ().addDragDetectListener ( new DragDetectListener () {
            public void dragDetected ( final DragDetectEvent e )
            {
                chart.getPlotArea ().setCursor ( dragCursor );
                dragStarted = true;
                dragStartedX = e.x;
                dragStartedY = e.y;
            }
        } );
        chart.getPlotArea ().addMouseListener ( new MouseListener () {
            public void mouseUp ( final MouseEvent e )
            {
                if ( dragStarted )
                {
                    dragStarted = false;
                    chart.getPlotArea ().setCursor ( null );
                    // zoom in range
                    DateRange zoomResult = zoomRange ( dragStartedX, e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                            .get ()
                                .getStartTime (), chartParameters.get ().getEndTime () );
                    ChartParameters parameters = ChartParameters
                            .create ()
                                .from ( chartParameters.get () )
                                .startTime ( zoomResult.getStart () )
                                .endTime ( zoomResult.getEnd () )
                                .construct ();
                    chartParameters.set ( parameters );
                    rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                }
                else
                {
                    if ( e.button == 1 )
                    {
                        // zoom in
                        DateRange zoomResult = zoomIn ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                                .get ()
                                    .getStartTime (), chartParameters.get ().getEndTime () );
                        ChartParameters parameters = ChartParameters
                                .create ()
                                    .from ( chartParameters.get () )
                                    .startTime ( zoomResult.getStart () )
                                    .endTime ( zoomResult.getEnd () )
                                    .construct ();
                        chartParameters.set ( parameters );
                        rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                    }
                    else if ( e.button == 3 )
                    {
                        // zoom out
                        DateRange zoomResult = zoomOut ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                                .get ()
                                    .getStartTime (), chartParameters.get ().getEndTime () );
                        ChartParameters parameters = ChartParameters
                                .create ()
                                    .from ( chartParameters.get () )
                                    .startTime ( zoomResult.getStart () )
                                    .endTime ( zoomResult.getEnd () )
                                    .construct ();
                        chartParameters.set ( parameters );
                        rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                    }
                }
            }

            public void mouseDown ( final MouseEvent e )
            {
            }

            public void mouseDoubleClick ( final MouseEvent e )
            {
            }
        } );

        // create predefined cursors
        dragCursor = new Cursor ( parent.getDisplay (), SWT.CURSOR_CROSS );

        // create predefined colors
        colorRegistry.put ( "MIN", new RGB ( 255, 0, 0 ) );
        colorRegistry.put ( "MAX", new RGB ( 0, 255, 0 ) );
        colorRegistry.put ( "AVG", new RGB ( 0, 0, 255 ) );

        // set up job for updating chart in case of parameter change
        parameterUpdateJob.set ( new Job ( "updateChartParameters" ) {
            @Override
            protected IStatus run ( final IProgressMonitor monitor )
            {
                doUpdateChartParameters ();
                return Status.OK_STATUS;
            }
        } );

        // set up job for updating chart in case of range change
        rangeUpdateJob.set ( new Job ( "updateRangeParameters" ) {
            @Override
            protected IStatus run ( final IProgressMonitor monitor )
            {
                doUpdateRangeParameters ();
                return Status.OK_STATUS;
            }
        } );

        // set up job for updating chart on data change
        dataUpdateJob.set ( new Job ( "updateChartData" ) {
            @Override
            protected IStatus run ( final IProgressMonitor monitor )
            {
                doUpdateChartData ();
                return Status.OK_STATUS;
            }
        } );

        // register all own listeners
        // according to selection on left side update chart as well
        getViewSite ().getWorkbenchWindow ().getSelectionService ().addSelectionListener ( new ISelectionListener () {
            public void selectionChanged ( final IWorkbenchPart part, final ISelection selection )
            {
                TrendView.this.setSelection ( selection );
            }
        } );
    }

    /**
     * FIXME: implement zoom out correctly, now its just a very primitive version of it
     * 
     * @param x position where clicked
     * @param xStart should be 0 in most cases (left edge of chart)
     * @param xEnd
     * @param startTime
     * @param endTime
     * @return
     */
    private DateRange zoomOut ( final int x, final int xStart, final int xEnd, final Date startTime, final Date endTime )
    {
        long dTimeQ = ( endTime.getTime () - startTime.getTime () ) / 4;
        return new DateRange ( new Date ( startTime.getTime () - dTimeQ ), new Date ( endTime.getTime () + dTimeQ ) );
    }

    /**
     * @param x
     * @param xStart
     * @param xEnd
     * @param startTime
     * @param endTime
     * @return
     */
    private DateRange zoomIn ( final int x, final int xStart, final int xEnd, final Date startTime, final Date endTime )
    {
        long factor = ( endTime.getTime () - startTime.getTime () ) / ( xEnd - xStart );
        long dTimeQ = ( endTime.getTime () - startTime.getTime () ) / 4;
        return new DateRange ( new Date ( startTime.getTime () + ( x * factor ) - dTimeQ ), new Date ( startTime
                .getTime () + ( x * factor ) + dTimeQ ) );
    }

    /**
     * @param drag1
     * @param drag2
     * @param xStart
     * @param xEnd
     * @param startTime
     * @param endTime
     * @return
     */
    private DateRange zoomRange ( final int drag1, final int drag2, final int xStart, final int xEnd, final Date startTime, final Date endTime )
    {
        long factor = ( endTime.getTime () - startTime.getTime () ) / ( xEnd - xStart );
        int dLeft = drag1;
        int dRight = drag2;
        if ( dLeft > dRight )
        {
            dLeft = drag2;
            dRight = drag1;
        }
        return new DateRange ( new Date ( startTime.getTime () + ( factor * dLeft ) ), new Date ( startTime.getTime () + ( factor * dRight ) ) );
    }

    @Override
    public void dispose ()
    {
        super.dispose ();
        parameterUpdateJob.get ().cancel ();
        rangeUpdateJob.get ().cancel ();
        dataUpdateJob.get ().cancel ();
    }

    protected void setSelection ( final ISelection selection )
    {
        clear ();
        if ( selection.isEmpty () )
        {
            return;
        }
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }
        final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
        if ( o instanceof QueryBufferBean )
        {
            setQuery ( ( (QueryBufferBean)o ) );
        }
    }

    private void setQuery ( final QueryBufferBean query )
    {
        this.query.set ( query );
        query.addQueryListener ( this );
    }

    private void clear ()
    {
        if ( query.get () != null )
        {
            query.get ().removeQueryListener ( this );
            query.set ( null );
        }
    }

    @Override
    public void setFocus ()
    {
    }

    // query listener

    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        // update model
        data.clear ();
        dataLabel = new Date[parameters.getEntries ()];
        for ( String seriesId : valueTypes )
        {
            data.put ( seriesId, new double[parameters.getEntries ()] );
        }
        ChartParameters newChartParameters = ChartParameters
                .create ()
                    .from ( chartParameters.get () )
                    .startTime ( parameters.getStartTimestamp ().getTime () )
                    .endTime ( parameters.getEndTimestamp ().getTime () )
                    .numOfEntries ( parameters.getEntries () )
                    .availableSeries ( valueTypes )
                    .construct ();
        if ( !newChartParameters.equals ( chartParameters.get () ) )
        {
            chartParameters.set ( newChartParameters );
            parameterUpdateJob.get ().schedule ( GUI_JOB_DELAY );
            rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
        }
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        for ( String seriesId : chartParameters.get ().getAvailableSeries () )
        {
            // use local ref for faster access
            final Value[] valueArray = values.get ( seriesId );
            final double[] chartValues = data.get ( seriesId );
            // now copy values from data source to our data array
            for ( int i = 0; i < valueInformation.length; i++ )
            {
                double value = valueArray[i].toDouble ();
                // at the moment special handling for values out of range,
                // should be handled by Chart
                if ( value >= Double.MAX_VALUE )
                {
                    value = 0;
                }
                if ( value <= -Double.MAX_VALUE )
                {
                    value = 0;
                }
                chartValues[i + index] = value;
            }
        }
        // now copy values for date axis
        for ( int i = 0; i < valueInformation.length; i++ )
        {
            dataLabel[i + index] = valueInformation[i].getStartTimestamp ().getTime ();
        }
        dataUpdateJob.get ().schedule ( GUI_JOB_DELAY );
    }

    public void updateState ( final QueryState state )
    {
    }

    // update chart parameters

    /**
     * must be run in GUI thread, does the actual modification of chart
     * parameters
     * @param parameters
     */
    private void doUpdateChartParameters ()
    {
        if ( parent.isDisposed () )
        {
            return;
        }
        final Display display = parent.getDisplay ();
        if ( display.isDisposed () )
        {
            return;
        }
        display.asyncExec ( new Runnable () {
            public void run ()
            {
                if ( parent.isDisposed () )
                {
                    return;
                }
                if ( panel.isDisposed () )
                {
                    return;
                }
                if ( chart.isDisposed () )
                {
                    return;
                }
                // update GUI with new parameters
                // remove old Series
                List<String> seriesIds = new ArrayList<String> ();
                for ( ISeries series : chart.getSeriesSet ().getSeries () )
                {
                    seriesIds.add ( series.getId () );
                }
                for ( String seriesId : seriesIds )
                {
                    chart.getSeriesSet ().deleteSeries ( seriesId );
                }
                for ( Group group : seriesGroups.values () )
                {
                    group.dispose ();
                }
                // add new series
                for ( final String seriesId : chartParameters.get ().getAvailableSeries () )
                {
                    final ILineSeries series = (ILineSeries)chart
                            .getSeriesSet ()
                                .createSeries ( SeriesType.LINE, seriesId );
                    series.setVisible ( true );
                    series.enableStep ( true );
                    series.setAntialias ( SWT.ON );
                    series.setSymbolType ( PlotSymbolType.NONE );
                    series.setLineColor ( colorRegistry.get ( seriesId ) );
                    Group group = new Group ( panel, SWT.SHADOW_ETCHED_IN );
                    seriesGroups.put ( seriesId, group );
                    group.setText ( seriesId );
                    group.setLayout ( groupLayout );
                    final Button colorButton = new Button ( group, SWT.PUSH );
                    colorButton.setText ( "Color" );
                    colorButton.setVisible ( true );
                    colorButton.setBackground ( colorRegistry.get ( seriesId ) );
                    colorButton.addSelectionListener ( new SelectionListener () {
                        public void widgetSelected ( final SelectionEvent e )
                        {
                            ColorDialog cd = new ColorDialog ( parent.getShell () );
                            cd.setText ( "Select color ..." );
                            RGB resultColor = cd.open ();
                            if ( resultColor != null )
                            {
                                colorRegistry.put ( seriesId, resultColor );
                                colorButton.setBackground ( colorRegistry.get ( seriesId ) );
                                series.setLineColor ( colorRegistry.get ( seriesId ) );
                            }
                        }

                        public void widgetDefaultSelected ( final SelectionEvent e )
                        {
                        }
                    } );
                    final Button visibleButton = new Button ( group, SWT.CHECK );
                    visibleButton.setText ( "Visible" );
                    visibleButton.setSelection ( true );
                    visibleButton.setVisible ( true );
                    visibleButton.addSelectionListener ( new SelectionListener () {
                        public void widgetSelected ( final SelectionEvent e )
                        {
                            if ( visibleButton.getSelection () )
                            {
                                series.setVisible ( true );
                            }
                            else
                            {
                                chart.getSeriesSet ().getSeries ( seriesId ).setVisible ( false );
                            }
                        }

                        public void widgetDefaultSelected ( final SelectionEvent e )
                        {
                        }
                    } );
                }
                chart.getTitle ().setText ( query.get ().getItemId () );
                chart.getAxisSet ().getYAxis ( 0 ).getTick ().setTickMarkStepHint ( 33 );
                chart.getAxisSet ().getXAxis ( 0 ).getTick ().setTickMarkStepHint ( 33 );
                parent.layout ( true, true );
            }
        } );
    }

    // range has changed, send feedback to query

    private void doUpdateRangeParameters ()
    {
        if ( query.get () != null )
        {
            Calendar startTime = new GregorianCalendar ();
            startTime.setTime ( chartParameters.get ().getStartTime () );
            Calendar endTime = new GregorianCalendar ();
            endTime.setTime ( chartParameters.get ().getEndTime () );
            query.get ().changeProperties ( new QueryParameters ( startTime, endTime, chartParameters
                    .get ()
                        .getNumOfEntries () ) );
        }
    }

    // update chart with new data

    private void doUpdateChartData ()
    {
        if ( chart.isDisposed () )
        {
            return;
        }
        final Display display = chart.getDisplay ();
        if ( display.isDisposed () )
        {
            return;
        }
        display.asyncExec ( new Runnable () {
            public void run ()
            {
                for ( String seriesId : chartParameters.get ().getAvailableSeries () )
                {
                    final ISeries series = chart.getSeriesSet ().getSeries ( seriesId );
                    // I'm not sure in which cases the series can even be null, but just try to continue as usual
                    if ( series == null )
                    {
                        continue;
                    }
                    series.setXDateSeries ( dataLabel );
                    series.setYSeries ( data.get ( seriesId ) );
                }
                IAxisTick xTick = chart.getAxisSet ().getXAxis ( 0 ).getTick ();
                xTick.setFormat ( new SimpleDateFormat ( formatByRange () ) );
                chart.getAxisSet ().adjustRange ();
                chart.redraw ();
            }

        } );
    }

    private String formatByRange ()
    {
        long range = chartParameters.get ().getEndTime ().getTime () - chartParameters
                .get ()
                    .getStartTime ()
                    .getTime ();
        if ( range < 1000 * 60 )
        {
            return "HH:mm:ss.SSS";
        }
        else if ( range < 1000 * 60 * 60 )
        {
            return "EEE HH:mm:ss";
        }
        else if ( range < 1000 * 60 * 60 * 12 )
        {
            return "dd. MMM HH:mm";
        }
        else
        {
            return "yyyy-MM-dd HH";
        }
    }
}
