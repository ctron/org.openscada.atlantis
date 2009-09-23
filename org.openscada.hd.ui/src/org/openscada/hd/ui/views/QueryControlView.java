package org.openscada.hd.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.ui.data.QueryBuffer;
import org.openscada.hd.ui.data.QueryBufferBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryControlView extends ViewPart implements QueryListener, PropertyChangeListener
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryControlView.class );

    private QueryBufferBean query;

    private Text startTimestampText;

    private Text endTimestampText;

    private Text entriesText;

    private Label stateText;

    private Text entriesRequestText;

    private Text endTimestampRequestText;

    private Text startTimestampRequestText;

    private QueryParameters requestParameters;

    private Button requestButton;

    private final Set<Control> controls = new HashSet<Control> ();

    public QueryControlView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        parent.setLayout ( new FillLayout ( SWT.VERTICAL ) );
        createControls ( parent );

        // disable all
        for ( final Control control : this.controls )
        {
            control.setEnabled ( false );
        }

        getViewSite ().getWorkbenchWindow ().getSelectionService ().addSelectionListener ( new ISelectionListener () {

            public void selectionChanged ( final IWorkbenchPart part, final ISelection selection )
            {
                QueryControlView.this.setSelection ( selection );
            }
        } );
    }

    private void createControls ( final Composite part )
    {
        final Composite parent = new Composite ( part, SWT.NONE );
        parent.setLayout ( new GridLayout ( 1, false ) );

        this.stateText = new Label ( parent, SWT.NONE );
        this.stateText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        Group group;

        // query group
        group = new Group ( parent, SWT.NONE );
        group.setText ( "Query" );
        group.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );
        group.setLayout ( new GridLayout ( 2, false ) );

        Label label;

        label = new Label ( group, SWT.NONE );
        label.setText ( "From: " );
        this.startTimestampText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.startTimestampText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( "To: " );
        this.endTimestampText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.endTimestampText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( "Entries: " );
        this.entriesText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.entriesText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        // request group
        group = new Group ( parent, SWT.NONE );
        group.setText ( "Request" );
        group.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );
        group.setLayout ( new GridLayout ( 2, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( "From: " );
        this.startTimestampRequestText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.startTimestampRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( "To: " );
        this.endTimestampRequestText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.endTimestampRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( "Entries: " );
        this.entriesRequestText = new Text ( group, SWT.BORDER );
        this.entriesRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );
        this.entriesRequestText.addVerifyListener ( new VerifyListener () {

            public void verifyText ( final VerifyEvent e )
            {
                e.doit = false;
                final StringBuilder text = new StringBuilder ();

                final String org = QueryControlView.this.entriesRequestText.getText ();
                text.append ( org );
                if ( org.equals ( "" ) && e.text.equals ( "" ) )
                {
                    e.doit = true;
                    return;
                }

                text.replace ( e.start, e.end, e.text );

                try
                {
                    final String str = text.toString ();

                    // empty string is ok
                    if ( str.equals ( "" ) )
                    {
                        e.doit = true;
                        return;
                    }

                    // check for int
                    final int i = Integer.parseInt ( text.toString () );
                    e.doit = i >= 0;
                }
                catch ( final NumberFormatException ex )
                {
                }
            }
        } );
        this.entriesRequestText.addModifyListener ( new ModifyListener () {

            public void modifyText ( final ModifyEvent e )
            {
                updateEntries ();
            }
        } );
        this.controls.add ( this.entriesRequestText );

        // controls
        final Composite compControl1 = new Composite ( group, SWT.NONE );
        compControl1.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
        compControl1.setLayout ( new FillLayout ( SWT.HORIZONTAL ) );

        createControlButton ( compControl1, "<<<", - ( 60 * 60 ), 0 );
        createControlButton ( compControl1, "<<", - ( 60 * 5 ), 0 );
        createControlButton ( compControl1, "<", -30, 0 );

        createControlButton ( compControl1, ">", 30, 0 );
        createControlButton ( compControl1, ">>", 60 * 5, 0 );
        createControlButton ( compControl1, ">>>", 60 * 60, 0 );

        final Composite compControl2 = new Composite ( group, SWT.NONE );
        compControl2.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
        compControl2.setLayout ( new FillLayout ( SWT.HORIZONTAL ) );

        createControlButton ( compControl2, "+", 0, 30 );
        createControlButton ( compControl2, "++", 0, 60 * 5 );
        createControlButton ( compControl2, "+++", 0, 60 * 60 );

        createControlButton ( compControl2, "-", 0, -30 );
        createControlButton ( compControl2, "--", 0, - ( 60 * 5 ) );
        createControlButton ( compControl2, "---", 0, - ( 60 * 60 ) );

        // action button

        this.requestButton = new Button ( group, SWT.BORDER | SWT.PUSH );
        this.requestButton.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
        this.requestButton.setText ( "Request" );
        this.requestButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                QueryControlView.this.updateRequest ();
            }
        } );
        this.controls.add ( this.requestButton );

    }

    private void createControlButton ( final Composite compControl, final String label, final int secondsOffset, final int secondsScale )
    {
        Button button;
        button = new Button ( compControl, SWT.BORDER | SWT.PUSH );
        button.setText ( label );
        button.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                QueryControlView.this.changeRequest ( secondsOffset, secondsScale );
            }
        } );
        this.controls.add ( button );
    }

    protected void changeRequest ( final int secondsOffset, final int secondsScale )
    {
        final Calendar start = this.requestParameters.getStartTimestamp ();
        final Calendar end = this.requestParameters.getEndTimestamp ();

        start.add ( Calendar.SECOND, secondsOffset );
        end.add ( Calendar.SECOND, secondsOffset );

        long diff = end.getTimeInMillis () - start.getTimeInMillis ();
        diff = diff / 1000;
        diff += secondsScale;
        if ( diff < 0 )
        {
            diff = 0;
        }
        diff *= 1000;
        end.setTimeInMillis ( start.getTimeInMillis () + diff );

        this.requestParameters = new QueryParameters ( start, end, this.requestParameters.getEntries () );
        updateRequestParameters ();
    }

    protected void updateRequest ()
    {
        this.query.changeProperties ( this.requestParameters );
    }

    private boolean updateEntries ()
    {
        try
        {
            final String text = this.entriesRequestText.getText ();
            this.requestParameters = new QueryParameters ( this.requestParameters.getStartTimestamp (), this.requestParameters.getEndTimestamp (), Integer.parseInt ( text ) );
            return true;
        }
        catch ( final NumberFormatException e )
        {
            return false;
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
        this.query = query;
        this.query.addQueryListener ( this );
        this.query.addPropertyChangeListener ( this );
        this.requestParameters = query.getRequestParameters ();

        for ( final Control control : this.controls )
        {
            control.setEnabled ( true );
        }

        updateRequestParameters ();
    }

    private void updateRequestParameters ()
    {
        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                QueryControlView.this.startTimestampRequestText.setText ( String.format ( "%tc", QueryControlView.this.requestParameters.getStartTimestamp () ) );
                QueryControlView.this.endTimestampRequestText.setText ( String.format ( "%tc", QueryControlView.this.requestParameters.getEndTimestamp () ) );
                QueryControlView.this.entriesRequestText.setText ( String.format ( "%d", QueryControlView.this.requestParameters.getEntries () ) );
            }
        } );
    }

    private void clear ()
    {
        if ( this.query != null )
        {
            this.query.removeQueryListener ( this );
            this.query.removePropertyChangeListener ( this );
            this.query = null;

            this.stateText.setText ( "" );
            this.startTimestampRequestText.setText ( "" );
            this.endTimestampRequestText.setText ( "" );
            this.entriesRequestText.setText ( "" );

            this.startTimestampText.setText ( "" );
            this.endTimestampText.setText ( "" );
            this.entriesText.setText ( "" );

            for ( final Control control : this.controls )
            {
                control.setEnabled ( false );
            }
        }
    }

    @Override
    public void setFocus ()
    {
        this.requestButton.setFocus ();
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
    }

    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                QueryControlView.this.startTimestampText.setText ( String.format ( "%tc", parameters.getStartTimestamp () ) );
                QueryControlView.this.endTimestampText.setText ( String.format ( "%tc", parameters.getEndTimestamp () ) );
                QueryControlView.this.entriesText.setText ( String.format ( "%d", parameters.getEntries () ) );
            }
        } );
    }

    private Display getDisplay ()
    {
        return getSite ().getShell ().getDisplay ();
    }

    public void updateState ( final QueryState state )
    {
        setState ( this.query.getState (), this.query.getPercentFilled () );
    }

    private void setState ( final QueryState state, final double percentFilled )
    {
        logger.info ( "Update state: {} / {}", state, percentFilled );

        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                QueryControlView.this.stateText.setText ( String.format ( "%s (%.2f%%)", state.toString (), 100.0 * percentFilled ) );
            }
        } );
    }

    public void propertyChange ( final PropertyChangeEvent evt )
    {
        if ( QueryBuffer.PROP_PERCENT_FILLED.equals ( evt.getPropertyName () ) || QueryBuffer.PROP_STATE.equals ( evt.getPropertyName () ) )
        {
            setState ( this.query.getState (), this.query.getPercentFilled () );
        }
    }

}
