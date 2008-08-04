package org.openscada.da.client.test.generator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;

public class BooleanGeneratorPage implements IGeneratorPage
{

    private Connection connection;

    private String itemId;

    private Button triggerTrue;

    private Button triggerFalse;

    private Spinner iterationsSpinner;

    private Spinner startDelaySpinner;

    private Spinner endDelaySpinner;

    private Button goButton;

    private Composite parent;
    
    private BooleanGenerator generator;

    public void createPage ( Composite parent )
    {
        this.parent = parent;
        parent.setLayout ( new FillLayout ( SWT.VERTICAL ) );
        createManualGroup ( parent );
        createTimedGroup ( parent );
        
        update ();
    }

    private void createTimedGroup ( Composite parent )
    {
        Group group = new Group ( parent, SWT.BORDER );
        group.setText ( "Timed" );

        group.setLayout ( new GridLayout ( 6, false ) );
        new Label ( group, SWT.NONE ).setText ( "Before Delay" );
        new Label ( group, SWT.NONE ).setText ( "0-1" );
        new Label ( group, SWT.NONE ).setText ( "After Delay" );
        new Label ( group, SWT.NONE ).setText ( "1-0" );
        new Label ( group, SWT.NONE ).setText ( "Iterations" );
        new Label ( group, SWT.NONE ).setText ( "" );

        this.startDelaySpinner = new Spinner ( group, SWT.BORDER );
        this.startDelaySpinner.setValues ( 1000, 0, Integer.MAX_VALUE, 0, 100, 1000 );
        new Label ( group, SWT.NONE ).setText ( "->" );
        this.endDelaySpinner = new Spinner ( group, SWT.BORDER );
        this.endDelaySpinner.setValues ( 1000, 0, Integer.MAX_VALUE, 0, 100, 1000 );
        new Label ( group, SWT.NONE ).setText ( "->" );
        this.iterationsSpinner = new Spinner ( group, SWT.BORDER );
        this.iterationsSpinner.setValues ( 100, 0, Integer.MAX_VALUE, 0, 5, 100 );
        this.goButton = new Button ( group, SWT.TOGGLE );
        goButton.setText ( "Go!" );
        goButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                BooleanGeneratorPage.this.toggleGo ();
            }
        } );
    }

    protected void toggleGo ()
    {
        if ( this.goButton.getSelection () )
        {
            int startDelay = this.startDelaySpinner.getSelection ();
            int endDelay = this.endDelaySpinner.getSelection ();
            int iterations = this.iterationsSpinner.getSelection ();
            generator = new BooleanGenerator ( parent.getDisplay (), this.connection, this.itemId );
            generator.setStartDelay ( startDelay );
            generator.setEndDelay ( endDelay );
            generator.setIterations ( iterations );
            generator.start ();
        }
        else
        {
            generator.dispose ();
            generator = null;
        }
        update ();
    }

    private void createManualGroup ( Composite parent )
    {
        Group group = new Group ( parent, SWT.BORDER );
        group.setText ( "Manual" );

        group.setLayout ( new RowLayout ( SWT.HORIZONTAL ) );

        triggerTrue = new Button ( group, SWT.BORDER );
        triggerTrue.setText ( "True" );
        triggerTrue.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                BooleanGeneratorPage.this.manualTriggerTrue ();
            }
        } );

        triggerFalse = new Button ( group, SWT.BORDER );
        triggerFalse.setText ( "False" );
        triggerFalse.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                BooleanGeneratorPage.this.manualTriggerFalse ();
            }
        } );
    }

    protected void manualTriggerFalse ()
    {
        writeValue ( new Variant ( false ) );
    }

    protected void manualTriggerTrue ()
    {
        writeValue ( new Variant ( true ) );
    }

    private void writeValue ( Variant variant )
    {
        this.connection.write ( itemId, variant, new WriteOperationCallback () {

            public void complete ()
            {
            }

            public void error ( Throwable e )
            {
            }

            public void failed ( String error )
            {
            }
        } );
    }

    public void dispose ()
    {
        if ( generator != null )
        {
            this.generator.dispose ();
            this.generator = null;
        }
    }

    public String getName ()
    {
        return "Boolean";
    }

    public void setDataItem ( Connection connection, String itemId )
    {
        this.connection = connection;
        this.itemId = itemId;

        update ();
    }

    private void update ()
    {
        this.startDelaySpinner.setEnabled ( this.generator == null );
        this.endDelaySpinner.setEnabled ( this.generator == null );
        this.iterationsSpinner.setEnabled ( this.generator == null );
        
        if ( this.generator != null )
        {
            triggerFalse.setEnabled ( false );
            triggerTrue.setEnabled ( false );
        }
        else
        {
            boolean enabled = this.connection != null && this.itemId != null;

            triggerFalse.setEnabled ( enabled );
            triggerTrue.setEnabled ( enabled );
        }
    }

}
