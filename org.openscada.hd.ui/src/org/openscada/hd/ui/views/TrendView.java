package org.openscada.hd.ui.views;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.chart.DataAtPoint;
import org.openscada.hd.chart.TrendChart;
import org.openscada.hd.ui.data.QueryBufferBean;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
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
        public final Date start;

        public final Date end;

        public DateRange ( final Date start, final Date end )
        {
            this.start = start;
            this.end = end;
        }
    }

    public static class SeriesParameters
    {
        public final String name;

        public final int width;

        public SeriesParameters ( final String name, final int width )
        {
            this.name = name;
            this.width = width;
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

        private final List<SeriesParameters> availableSeries = new ArrayList<SeriesParameters> ();

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
                this.seriesParameters ( parameters.availableSeries );
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

            public ChartParameterBuilder seriesParameters ( final Iterable<SeriesParameters> availableSeries )
            {
                parameters.availableSeries.clear ();
                for ( SeriesParameters series : availableSeries )
                {
                    parameters.availableSeries.add ( series );
                }
                return this;
            }

            public ChartParameterBuilder seriesParameters ( final SeriesParameters newSeriesParameters )
            {
                int index = 0;
                for ( SeriesParameters oldSeriesParameters : parameters.availableSeries )
                {
                    if ( oldSeriesParameters.name.equals ( newSeriesParameters.name ) )
                    {
                        parameters.availableSeries.set ( index, newSeriesParameters );
                    }
                    index += 1;
                }
                return this;
            }

            public ChartParameterBuilder seriesWidth ( final String seriesId, final int width )
            {
                this.seriesParameters ( new SeriesParameters ( seriesId, width ) );
                return this;
            }

            public ChartParameterBuilder availableSeries ( final Iterable<String> availableSeries )
            {
                List<SeriesParameters> newSeriesParameters = new ArrayList<SeriesParameters> ();
                for ( String seriesId : availableSeries )
                {
                    boolean found = false;
                    for ( SeriesParameters seriesParameters : parameters.availableSeries )
                    {
                        if ( seriesId.equals ( seriesParameters.name ) )
                        {
                            newSeriesParameters.add ( seriesParameters );
                            found = true;
                        }

                    }
                    if ( !found )
                    {
                        newSeriesParameters.add ( new SeriesParameters ( seriesId, 1 ) );
                    }
                }
                parameters.availableSeries.clear ();
                parameters.availableSeries.addAll ( newSeriesParameters );
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

        public List<SeriesParameters> getAvailableSeries ()
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

    private final AtomicReference<double[]> dataQuality = new AtomicReference<double[]> ();

    private final AtomicReference<Date[]> dataTimestamp = new AtomicReference<Date[]> ();

    private final AtomicReference<ChartParameters> chartParameters = new AtomicReference<ChartParameters> ();

    // gui
    private Composite parent;

    private Composite panel;

    private Group qualityGroup;

    private RowLayout groupLayout;

    private Spinner qualitySpinner;

    private Button qualityColorButton;

    private TrendChart chart;

    private final ConcurrentMap<String, Group> seriesGroups = new ConcurrentHashMap<String, Group> ();

    private Cursor dragCursor;

    private Cursor zoomInCursor;

    private Cursor zoomOutCursor;

    private volatile boolean dragStarted = false;

    private volatile int dragStartedX = -1;

    private FontRegistry fontRegistry;

    private ColorRegistry colorRegistry;

    @Override
    public void createPartControl ( final Composite parent )
    {
        // create predefined cursors
        dragCursor = new Cursor ( parent.getDisplay (), SWT.CURSOR_HAND );
        ImageData zoomInImage = new ImageData ( getClass ()
                .getClassLoader ()
                    .getResourceAsStream ( "org/openscada/hd/ui/zoomin.gif" ) );
        zoomInCursor = new Cursor ( parent.getDisplay (), zoomInImage, 15, 15 );
        ImageData zoomOutImage = new ImageData ( getClass ()
                .getClassLoader ()
                    .getResourceAsStream ( "org/openscada/hd/ui/zoomout.gif" ) );
        zoomOutCursor = new Cursor ( parent.getDisplay (), zoomOutImage, 15, 15 );

        // create predefined colors
        colorRegistry = new ColorRegistry ( parent.getDisplay () );
        colorRegistry.put ( "quality", new RGB ( 255, 192, 192 ) );
        colorRegistry.put ( "MIN", new RGB ( 255, 0, 0 ) );
        colorRegistry.put ( "MAX", new RGB ( 0, 255, 0 ) );
        colorRegistry.put ( "AVG", new RGB ( 0, 0, 255 ) );

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

        groupLayout = new RowLayout ( SWT.HORIZONTAL );
        groupLayout.center = true;

        // add label for Spinner
        qualityGroup = new Group ( panel, SWT.SHADOW_ETCHED_IN );

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
                    IBarSeries series = (IBarSeries)chart.getSeriesSet ().getSeries ( "quality" );
                    if ( series != null )
                    {
                        series.setBarColor ( colorRegistry.get ( "quality" ) );
                    }
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
        chart = new TrendChart ( parent, SWT.NONE );
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

        IAxis qualityYAxis = chart.getAxisSet ().getYAxis ( chart.getAxisSet ().createYAxis () );
        IAxis qualityXAxis = chart.getAxisSet ().getXAxis ( chart.getAxisSet ().createXAxis () );
        IBarSeries qualitySeries = (IBarSeries)chart.getSeriesSet ().createSeries ( SeriesType.BAR, "quality" );
        qualitySeries.setBarColor ( colorRegistry.get ( "quality" ) );
        qualitySeries.setBarPadding ( 0 );
        qualitySeries.setYAxisId ( qualityYAxis.getId () );
        qualitySeries.setXAxisId ( qualityXAxis.getId () );
        qualityYAxis.getTitle ().setVisible ( false );
        qualityYAxis.getTick ().setVisible ( false );
        qualityYAxis.getGrid ().setStyle ( LineStyle.NONE );
        qualityXAxis.getTitle ().setVisible ( false );
        qualityXAxis.getTick ().setVisible ( false );
        qualityXAxis.getGrid ().setStyle ( LineStyle.NONE );

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
        chart.getPlotArea ().addKeyListener ( new KeyListener () {
            public void keyReleased ( final KeyEvent e )
            {
                if ( e.keyCode == SWT.SHIFT )
                {
                    chart.getPlotArea ().setCursor ( null );
                }
                else if ( e.keyCode == SWT.ALT )
                {
                    chart.getPlotArea ().setCursor ( null );
                }
            }

            public void keyPressed ( final KeyEvent e )
            {
                if ( e.keyCode == SWT.SHIFT )
                {
                    chart.getPlotArea ().setCursor ( zoomInCursor );
                }
                else if ( e.keyCode == SWT.ALT )
                {
                    chart.getPlotArea ().setCursor ( zoomOutCursor );
                }
            }
        } );
        chart.getPlotArea ().addMouseTrackListener ( new MouseTrackListener () {
            public void mouseHover ( final MouseEvent e )
            {
                if ( ( e.stateMask & SWT.SHIFT ) == SWT.SHIFT )
                {
                    chart.getPlotArea ().setCursor ( zoomInCursor );
                }
                else if ( ( e.stateMask & SWT.ALT ) == SWT.ALT )
                {
                    chart.getPlotArea ().setCursor ( zoomOutCursor );
                }
            }

            public void mouseExit ( final MouseEvent e )
            {
                chart.getPlotArea ().setCursor ( null );
            }

            public void mouseEnter ( final MouseEvent e )
            {
                chart.getPlotArea ().setFocus ();
            }
        } );
        chart.getPlotArea ().addDragDetectListener ( new DragDetectListener () {
            public void dragDetected ( final DragDetectEvent e )
            {
                chart.getPlotArea ().setCursor ( dragCursor );
                dragStarted = true;
                dragStartedX = e.x;
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
                    DateRange zoomResult = moveRange ( dragStartedX, e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                            .get ()
                                .getStartTime (), chartParameters.get ().getEndTime () );
                    ChartParameters parameters = ChartParameters
                            .create ()
                                .from ( chartParameters.get () )
                                .startTime ( zoomResult.start )
                                .endTime ( zoomResult.end )
                                .construct ();
                    chartParameters.set ( parameters );
                    rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                }
                else
                {
                    if ( ( e.button == 1 ) && ( ( e.stateMask & SWT.SHIFT ) == SWT.SHIFT ) )
                    {
                        // zoom in
                        DateRange zoomResult = zoomIn ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                                .get ()
                                    .getStartTime (), chartParameters.get ().getEndTime () );
                        ChartParameters parameters = ChartParameters
                                .create ()
                                    .from ( chartParameters.get () )
                                    .startTime ( zoomResult.start )
                                    .endTime ( zoomResult.end )
                                    .construct ();
                        chartParameters.set ( parameters );
                        rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                    }
                    else if ( ( e.button == 1 ) && ( ( e.stateMask & SWT.ALT ) == SWT.ALT ) )
                    {
                        // zoom out
                        DateRange zoomResult = zoomOut ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                                .get ()
                                    .getStartTime (), chartParameters.get ().getEndTime () );
                        ChartParameters parameters = ChartParameters
                                .create ()
                                    .from ( chartParameters.get () )
                                    .startTime ( zoomResult.start )
                                    .endTime ( zoomResult.end )
                                    .construct ();
                        chartParameters.set ( parameters );
                        rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                    }
                    else if ( e.button == 1 )
                    {
                        chart.getPlotArea ().setCursor ( null );
                        // zoom in range
                        DateRange zoomResult = moveRange ( e.x, chart.getPlotArea ().getSize ().x / 2, 0, chart
                                .getPlotArea ()
                                    .getSize ().x, chartParameters.get ().getStartTime (), chartParameters
                                .get ()
                                    .getEndTime () );
                        ChartParameters parameters = ChartParameters
                                .create ()
                                    .from ( chartParameters.get () )
                                    .startTime ( zoomResult.start )
                                    .endTime ( zoomResult.end )
                                    .construct ();
                        chartParameters.set ( parameters );
                        rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                    }
                    else if ( e.button == 3 )
                    {
                        chart.getMenu ().setVisible ( true );
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
        chart.getPlotArea ().addMouseWheelListener ( new MouseWheelListener () {
            public void mouseScrolled ( final MouseEvent e )
            {
                if ( e.count > 0 )
                {
                    // zoom in
                    DateRange zoomResult = zoomIn ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                            .get ()
                                .getStartTime (), chartParameters.get ().getEndTime () );
                    ChartParameters parameters = ChartParameters
                            .create ()
                                .from ( chartParameters.get () )
                                .startTime ( zoomResult.start )
                                .endTime ( zoomResult.end )
                                .construct ();
                    chartParameters.set ( parameters );
                    rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                }
                else
                {
                    // zoom out
                    DateRange zoomResult = zoomOut ( e.x, 0, chart.getPlotArea ().getSize ().x, chartParameters
                            .get ()
                                .getStartTime (), chartParameters.get ().getEndTime () );
                    ChartParameters parameters = ChartParameters
                            .create ()
                                .from ( chartParameters.get () )
                                .startTime ( zoomResult.start )
                                .endTime ( zoomResult.end )
                                .construct ();
                    chartParameters.set ( parameters );
                    rangeUpdateJob.get ().schedule ( GUI_JOB_DELAY );
                }
            }
        } );

        chart.setDataAtPoint ( new DataAtPoint () {
            private int coordinateToIndex ( final int x )
            {
                final int margin = 10;
                try
                {
                    int numOfEntries = chartParameters.get ().getNumOfEntries ();
                    int pixels = chart.getPlotArea ().getBounds ().width - ( 2 * margin );
                    double factor = (double)numOfEntries / (double)pixels;
                    final int i = (int)Math.round ( ( x - margin ) * factor );
                    return i;
                }
                catch ( Exception e )
                {
                    // pass
                }
                return 0;
            }

            public Date getTimestamp ( final int x )
            {
                return dataTimestamp.get ()[coordinateToIndex ( x )];
            }

            public double getQuality ( final int x )
            {
                return dataQuality.get ()[coordinateToIndex ( x )];
            }

            public Map<String, Double> getData ( final int x )
            {
                Map<String, Double> result = new HashMap<String, Double> ();
                for ( SeriesParameters seriesParameters : chartParameters.get ().getAvailableSeries () )
                {
                    result.put ( seriesParameters.name, data.get ( seriesParameters.name )[coordinateToIndex ( x )] );
                }
                return result;
            }
        } );

        Menu m = new Menu ( chart );
        MenuItem miSaveAsImage = new MenuItem ( m, SWT.NONE );
        miSaveAsImage.setText ( "Save as image ..." );
        miSaveAsImage.addSelectionListener ( new SelectionListener () {
            public void widgetSelected ( final SelectionEvent e )
            {
                FileDialog dlg = new FileDialog ( parent.getShell () );
                dlg.setText ( "Save Trend As Image" );
                String filename = dlg.open ();
                if ( filename != null )
                {
                    File file = new File ( filename );
                    try
                    {
                        if ( file.canWrite () || file.createNewFile () )
                        {
                            chart.update ();
                            chart.save ( filename, SWT.IMAGE_PNG );
                        }
                        else
                        {
                            MessageDialog
                                    .openError ( parent.getShell (), "Save Trend As Image", "File could not be saved!" );
                        }
                    }
                    catch ( IOException ex )
                    {
                        MessageDialog
                                .openError ( parent.getShell (), "Save Trend As Image", "File could not be saved!" );
                    }
                }
            }

            public void widgetDefaultSelected ( final SelectionEvent e )
            {

            }
        } );
        MenuItem miPrint = new MenuItem ( m, SWT.NONE );
        miPrint.setText ( "Print" );
        miPrint.addSelectionListener ( new SelectionListener () {
            public void widgetSelected ( final SelectionEvent e )
            {
                PrintDialog printDialog = new PrintDialog ( parent.getShell () );
                // and open it
                PrinterData printerData = printDialog.open ();
                // Check if OK was pressed
                if ( printerData != null )
                {
                    MessageDialog.openInformation ( parent.getShell (), "Print Trend", "Not implemented!" );
                }
            }

            public void widgetDefaultSelected ( final SelectionEvent e )
            {
            }
        } );

        chart.setMenu ( m );

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
    private DateRange zoomOut ( final int x, final int xStart, final int xEnd, Date startTime, Date endTime )
    {
        long factor = ( endTime.getTime () - startTime.getTime () ) / ( xEnd - xStart );
        long dTimeQ = ( endTime.getTime () - startTime.getTime () ) / 4;
        long timeshift = factor * ( x - ( xEnd - xStart ) / 2 );
        startTime = new Date ( startTime.getTime () + timeshift );
        endTime = new Date ( endTime.getTime () + timeshift );
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
    private DateRange moveRange ( final int drag1, final int drag2, final int xStart, final int xEnd, final Date startTime, final Date endTime )
    {
        long factor = ( endTime.getTime () - startTime.getTime () ) / ( xEnd - xStart );
        long timediff = Math.abs ( ( drag1 - drag2 ) ) * factor;
        if ( drag2 > drag1 )
        {
            return new DateRange ( new Date ( startTime.getTime () - timediff ), new Date ( endTime.getTime () - timediff ) );
        }
        else
        {
            return new DateRange ( new Date ( startTime.getTime () + timediff ), new Date ( endTime.getTime () + timediff ) );
        }
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
        dataTimestamp.set ( new Date[parameters.getEntries ()] );
        dataQuality.set ( new double[parameters.getEntries ()] );
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
        for ( SeriesParameters series : chartParameters.get ().getAvailableSeries () )
        {
            // use local ref for faster access
            final Value[] valueArray = values.get ( series.name );
            final double[] chartValues = data.get ( series.name );
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
        // now copy values for date axis and quality
        for ( int i = 0; i < valueInformation.length; i++ )
        {
            dataTimestamp.get ()[i + index] = valueInformation[i].getStartTimestamp ().getTime ();
            dataQuality.get ()[i + index] = valueInformation[i].getQuality ();
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
                    if ( "quality".equals ( series.getId () ) )
                    {
                        continue;
                    }
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
                for ( final SeriesParameters seriesParameters : chartParameters.get ().getAvailableSeries () )
                {
                    final ILineSeries series = (ILineSeries)chart
                            .getSeriesSet ()
                                .createSeries ( SeriesType.LINE, seriesParameters.name );
                    series.setYAxisId ( 0 );
                    series.setXAxisId ( 0 );
                    series.setVisible ( seriesParameters.width > 0 );
                    series.enableStep ( true );
                    series.setAntialias ( SWT.ON );
                    series.setSymbolType ( PlotSymbolType.NONE );
                    series.setLineColor ( colorRegistry.get ( seriesParameters.name ) );
                    series.setLineWidth ( seriesParameters.width );
                    Group group = new Group ( panel, SWT.SHADOW_ETCHED_IN );
                    seriesGroups.put ( seriesParameters.name, group );
                    group.setText ( seriesParameters.name );
                    group.setLayout ( groupLayout );
                    final Button colorButton = new Button ( group, SWT.PUSH );
                    colorButton.setText ( "Color" );
                    colorButton.setVisible ( true );
                    colorButton.setBackground ( colorRegistry.get ( seriesParameters.name ) );
                    colorButton.addSelectionListener ( new SelectionListener () {
                        public void widgetSelected ( final SelectionEvent e )
                        {
                            ColorDialog cd = new ColorDialog ( parent.getShell () );
                            cd.setText ( "Select color ..." );
                            RGB resultColor = cd.open ();
                            if ( resultColor != null )
                            {
                                colorRegistry.put ( seriesParameters.name, resultColor );
                                colorButton.setBackground ( colorRegistry.get ( seriesParameters.name ) );
                                series.setLineColor ( colorRegistry.get ( seriesParameters.name ) );
                                chart.redraw ();
                            }
                        }

                        public void widgetDefaultSelected ( final SelectionEvent e )
                        {
                        }
                    } );
                    final Spinner widthSpinner = new Spinner ( group, SWT.BORDER );
                    widthSpinner.setDigits ( 0 );
                    widthSpinner.setMinimum ( 0 );
                    widthSpinner.setMaximum ( 25 );
                    widthSpinner.setSelection ( seriesParameters.width );
                    widthSpinner.addSelectionListener ( new SelectionListener () {
                        public void widgetSelected ( final SelectionEvent e )
                        {
                            ChartParameters newChartParameters = ChartParameters
                                    .create ()
                                        .from ( chartParameters.get () )
                                        .seriesWidth ( seriesParameters.name, widthSpinner.getSelection () )
                                        .construct ();
                            chartParameters.set ( newChartParameters );
                            series.setLineWidth ( widthSpinner.getSelection () );
                            series.setVisible ( widthSpinner.getSelection () > 0 );
                            chart.redraw ();
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
                for ( SeriesParameters seriesParameter : chartParameters.get ().getAvailableSeries () )
                {
                    final ISeries series = chart.getSeriesSet ().getSeries ( seriesParameter.name );
                    // I'm not sure in which cases the series can even be null, but just try to continue as usual
                    if ( series == null )
                    {
                        continue;
                    }
                    series.setXDateSeries ( dataTimestamp.get () );
                    series.setYSeries ( data.get ( seriesParameter.name ) );
                }
                chart.getAxisSet ().getXAxis ( 0 ).getTick ().setFormat ( new SimpleDateFormat ( formatByRange () ) );
                double quality = chartParameters.get ().getQuality ();
                ISeries qualitySeries = chart.getSeriesSet ().getSeries ( "quality" );
                if ( qualitySeries != null )
                {
                    qualitySeries.setYSeries ( qualityData ( dataQuality.get (), quality ) );
                    qualitySeries.setXDateSeries ( dataTimestamp.get () );
                    if ( quality > 0.0 )
                    {
                        qualitySeries.setVisible ( true );
                    }
                    else
                    {
                        qualitySeries.setVisible ( false );
                    }
                }
                chart.getAxisSet ().adjustRange ();
                chart.redraw ();
            }
        } );
    }

    /**
     * returns array of 1s or 0s. An element is 1 if the quality in the given position is lower then the quality parameter
     * this is misused to draw the bar chart which displays the quality
     * @param data
     * @param quality
     * @return
     */
    private double[] qualityData ( final double[] data, final double quality )
    {
        double[] result = new double[data.length];
        int i = 0;
        for ( double q : data )
        {
            if ( q < ( quality / 100.0 ) )
            {
                result[i] = 1;
            }
            else
            {
                result[i] = 0;
            }
            i += 1;
        }
        return result;
    }

    /**
     * tries to adjust labels for x axis according to range
     * @return
     */
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

    @Override
    public void dispose ()
    {
        super.dispose ();
        parameterUpdateJob.get ().cancel ();
        rangeUpdateJob.get ().cancel ();
        dataUpdateJob.get ().cancel ();
        dragCursor.dispose ();
        zoomInCursor.dispose ();
        zoomOutCursor.dispose ();
    }
}
