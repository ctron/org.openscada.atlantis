package org.openscada.hd.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.ui.data.QueryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryControlView extends QueryViewPart implements PropertyChangeListener
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryControlView.class );

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

        addListener ();
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
        group.setText ( Messages.QueryControlView_Group_Query_Text );
        group.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );
        group.setLayout ( new GridLayout ( 2, false ) );

        Label label;

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_From_Text );
        this.startTimestampText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.startTimestampText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_To_Text );
        this.endTimestampText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.endTimestampText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_Entries_Text );
        this.entriesText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.entriesText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        // request group
        group = new Group ( parent, SWT.NONE );
        group.setText ( Messages.QueryControlView_Group_Request_Text );
        group.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );
        group.setLayout ( new GridLayout ( 2, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_From_Text );
        this.startTimestampRequestText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.startTimestampRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_To_Text );
        this.endTimestampRequestText = new Text ( group, SWT.BORDER | SWT.READ_ONLY );
        this.endTimestampRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );

        label = new Label ( group, SWT.NONE );
        label.setText ( Messages.QueryControlView_Label_Entries_Text );
        this.entriesRequestText = new Text ( group, SWT.BORDER );
        this.entriesRequestText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );
        this.entriesRequestText.addVerifyListener ( new VerifyListener () {

            public void verifyText ( final VerifyEvent e )
            {
                e.doit = false;
                final StringBuilder text = new StringBuilder ();

                final String org = QueryControlView.this.entriesRequestText.getText ();
                text.append ( org );
                if ( org.equals ( "" ) && e.text.equals ( "" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    e.doit = true;
                    return;
                }

                text.replace ( e.start, e.end, e.text );

                try
                {
                    final String str = text.toString ();

                    // empty string is ok
                    if ( str.equals ( "" ) ) //$NON-NLS-1$
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

        createControlButton ( compControl1, Messages.QueryControlView_PaneLeft3_Button_Text, - ( 60 * 60 ), 0 );
        createControlButton ( compControl1, Messages.QueryControlView_PaneLeft2_Button_Text, - ( 60 * 5 ), 0 );
        createControlButton ( compControl1, Messages.QueryControlView_PaneLeft1_Button_Text, -30, 0 );

        createControlButton ( compControl1, Messages.QueryControlView_PaneRight1_Button_Text, 30, 0 );
        createControlButton ( compControl1, Messages.QueryControlView_PaneRight2_Button_Text, 60 * 5, 0 );
        createControlButton ( compControl1, Messages.QueryControlView_PaneRight3_Button_Text, 60 * 60, 0 );

        final Composite compControl2 = new Composite ( group, SWT.NONE );
        compControl2.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
        compControl2.setLayout ( new FillLayout ( SWT.HORIZONTAL ) );

        createControlButton ( compControl2, Messages.QueryControlView_Add1_Button_Text, 0, 30 );
        createControlButton ( compControl2, Messages.QueryControlView_Add2_Button_Text, 0, 60 * 5 );
        createControlButton ( compControl2, Messages.QueryControlView_Add3_Button_Text, 0, 60 * 60 );

        createControlButton ( compControl2, Messages.QueryControlView_Remove1_Button_Text, 0, -30 );
        createControlButton ( compControl2, Messages.QueryControlView_Remove2_Button_Text, 0, - ( 60 * 5 ) );
        createControlButton ( compControl2, Messages.QueryControlView_Remove3_Button_Text, 0, - ( 60 * 60 ) );

        // action button

        this.requestButton = new Button ( group, SWT.BORDER | SWT.PUSH );
        this.requestButton.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );
        this.requestButton.setText ( Messages.QueryControlView_Request_Button_Text );
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

    private void updateRequestParameters ()
    {
        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                QueryControlView.this.startTimestampRequestText.setText ( String.format ( Messages.QueryControlView_Format_Request_Date, QueryControlView.this.requestParameters.getStartTimestamp () ) );
                QueryControlView.this.endTimestampRequestText.setText ( String.format ( Messages.QueryControlView_Format_Request_Date, QueryControlView.this.requestParameters.getEndTimestamp () ) );
                QueryControlView.this.entriesRequestText.setText ( String.format ( Messages.QueryControlView_Format_Request_Entries, QueryControlView.this.requestParameters.getEntries () ) );
            }
        } );
    }

    @Override
    protected void setQuery ( final QueryBuffer query )
    {
        super.setQuery ( query );
        this.query.addPropertyChangeListener ( this );

        for ( final Control control : this.controls )
        {
            control.setEnabled ( true );
        }

        this.requestParameters = query.getRequestParameters ();
        updateRequestParameters ();
    }

    @Override
    protected void clear ()
    {
        if ( this.query != null )
        {
            this.query.removePropertyChangeListener ( this );

            this.stateText.setText ( Messages.QueryControlView_EmptyString );
            this.startTimestampRequestText.setText ( Messages.QueryControlView_EmptyString );
            this.endTimestampRequestText.setText ( Messages.QueryControlView_EmptyString );
            this.entriesRequestText.setText ( Messages.QueryControlView_EmptyString );

            this.startTimestampText.setText ( Messages.QueryControlView_EmptyString );
            this.endTimestampText.setText ( Messages.QueryControlView_EmptyString );
            this.entriesText.setText ( Messages.QueryControlView_EmptyString );

            for ( final Control control : this.controls )
            {
                control.setEnabled ( false );
            }
        }

        super.clear ();
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
                QueryControlView.this.startTimestampText.setText ( String.format ( Messages.QueryControlView_Format_Query_Date, parameters.getStartTimestamp () ) );
                QueryControlView.this.endTimestampText.setText ( String.format ( Messages.QueryControlView_Format_Query_Date, parameters.getEndTimestamp () ) );
                QueryControlView.this.entriesText.setText ( String.format ( Messages.QueryControlView_Format_Query_Entries, parameters.getEntries () ) );
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
        logger.info ( "Update state: {} / {}", state, percentFilled ); //$NON-NLS-1$

        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                QueryControlView.this.stateText.setText ( String.format ( Messages.QueryControlView_Format_StateString, state.toString (), 100.0 * percentFilled ) );
            }
        } );
    }

    public void propertyChange ( final PropertyChangeEvent evt )
    {
        if ( QueryBuffer.PROP_PERCENT_FILLED.equals ( evt.getPropertyName () ) || QueryBuffer.PROP_STATE.equals ( evt.getPropertyName () ) )
        {
            setState ( this.query.getState (), this.query.getPercentFilled () );
        }
        else if ( QueryBuffer.PROP_REQUEST_PARAMETERS.equals ( evt.getPropertyName () ) )
        {
            logger.info ( "Request set using property change: {}", this.query.getRequestParameters () ); //$NON-NLS-1$
            this.requestParameters = this.query.getRequestParameters ();
            updateRequestParameters ();
        }
    }

}
