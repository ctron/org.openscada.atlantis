package org.openscada.da.client.signalgenerator.page;

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
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.base.item.DataItemHolder;

public class BooleanGeneratorPage implements GeneratorPage
{
    private Button triggerTrue;

    private Button triggerFalse;

    private Spinner iterationsSpinner;

    private Spinner startDelaySpinner;

    private Spinner endDelaySpinner;

    private Button goButton;

    private Composite parent;

    private BooleanGenerator generator;

    private DataItemHolder item;

    public void createPage ( final Composite parent )
    {
        this.parent = parent;
        parent.setLayout ( new FillLayout ( SWT.VERTICAL ) );
        createManualGroup ( parent );
        createTimedGroup ( parent );

        update ();
    }

    private void createTimedGroup ( final Composite parent )
    {
        final Group group = new Group ( parent, SWT.BORDER );
        group.setText ( Messages.getString ( "BooleanGeneratorPage.groupTimed.text" ) ); //$NON-NLS-1$

        group.setLayout ( new GridLayout ( 6, false ) );
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.beforeDelay" ) ); //$NON-NLS-1$
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.0to1" ) ); //$NON-NLS-1$
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.afterDelay" ) ); //$NON-NLS-1$
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.1to0" ) ); //$NON-NLS-1$
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.iterations" ) ); //$NON-NLS-1$
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.after" ) ); //$NON-NLS-1$

        this.startDelaySpinner = new Spinner ( group, SWT.BORDER );
        this.startDelaySpinner.setValues ( 1000, 0, Integer.MAX_VALUE, 0, 100, 1000 );
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.transition" ) ); //$NON-NLS-1$
        this.endDelaySpinner = new Spinner ( group, SWT.BORDER );
        this.endDelaySpinner.setValues ( 1000, 0, Integer.MAX_VALUE, 0, 100, 1000 );
        new Label ( group, SWT.NONE ).setText ( Messages.getString ( "BooleanGeneratorPage.timedGroup.transition" ) ); //$NON-NLS-1$
        this.iterationsSpinner = new Spinner ( group, SWT.BORDER );
        this.iterationsSpinner.setValues ( 100, 0, Integer.MAX_VALUE, 0, 5, 100 );
        this.goButton = new Button ( group, SWT.TOGGLE );
        this.goButton.setText ( Messages.getString ( "BooleanGeneratorPage.goButton.text" ) ); //$NON-NLS-1$
        this.goButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                BooleanGeneratorPage.this.toggleGo ();
            }
        } );
    }

    protected void toggleGo ()
    {
        if ( this.goButton.getSelection () )
        {
            final int startDelay = this.startDelaySpinner.getSelection ();
            final int endDelay = this.endDelaySpinner.getSelection ();
            final int iterations = this.iterationsSpinner.getSelection ();
            this.generator = new BooleanGenerator ( this.parent.getDisplay (), this.item );
            this.generator.setStartDelay ( startDelay );
            this.generator.setEndDelay ( endDelay );
            this.generator.setIterations ( iterations );
            this.generator.start ();
        }
        else
        {
            this.generator.dispose ();
            this.generator = null;
        }
        update ();
    }

    private void createManualGroup ( final Composite parent )
    {
        final Group group = new Group ( parent, SWT.BORDER );
        group.setText ( Messages.getString ( "BooleanGeneratorPage.manualGroup.text" ) ); //$NON-NLS-1$

        group.setLayout ( new RowLayout ( SWT.HORIZONTAL ) );

        this.triggerTrue = new Button ( group, SWT.BORDER );
        this.triggerTrue.setText ( Messages.getString ( "BooleanGeneratorPage.manualGroup.triggerTrue.text" ) ); //$NON-NLS-1$
        this.triggerTrue.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                BooleanGeneratorPage.this.manualTriggerTrue ();
            }
        } );

        this.triggerFalse = new Button ( group, SWT.BORDER );
        this.triggerFalse.setText ( Messages.getString ( "BooleanGeneratorPage.manualGroup.triggerFalse.text" ) ); //$NON-NLS-1$
        this.triggerFalse.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
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

    private void writeValue ( final Variant variant )
    {
        this.item.getConnection ().write ( this.item.getItemId (), variant, new WriteOperationCallback () {

            public void complete ()
            {
            }

            public void error ( final Throwable e )
            {
            }

            public void failed ( final String error )
            {
            }
        } );
    }

    public void dispose ()
    {
        if ( this.generator != null )
        {
            this.generator.dispose ();
            this.generator = null;
        }
    }

    public void setDataItem ( final DataItemHolder item )
    {
        this.item = item;
        update ();
    }

    private void update ()
    {
        this.startDelaySpinner.setEnabled ( this.generator == null );
        this.endDelaySpinner.setEnabled ( this.generator == null );
        this.iterationsSpinner.setEnabled ( this.generator == null );

        if ( this.generator != null )
        {
            this.triggerFalse.setEnabled ( false );
            this.triggerTrue.setEnabled ( false );
        }
        else
        {
            final boolean enabled = this.item != null && this.item.getConnection () != null && this.item.getItemId () != null;

            this.triggerFalse.setEnabled ( enabled );
            this.triggerTrue.setEnabled ( enabled );
        }
    }

}
