package org.openscada.da.client.test.generator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;

public class BooleanGeneratorPage implements IGeneratorPage
{

    private Connection connection;

    private String itemId;

    private Button triggerTrue;

    private Button triggerFalse;

    public void createPage ( Composite parent )
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

        update ();
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
        boolean enabled = this.connection != null && this.itemId != null;

        triggerFalse.setEnabled ( enabled );
        triggerTrue.setEnabled ( enabled );
    }

}
