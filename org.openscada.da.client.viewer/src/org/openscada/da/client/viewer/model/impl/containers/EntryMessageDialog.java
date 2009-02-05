package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.VariantSetterOutput;

public class EntryMessageDialog extends PopupCommandDialog
{
    private VariantSetterOutput _output;
    private Text _textEntry;
    
    public EntryMessageDialog ( Shell parent, VariantSetterOutput output, Point initialLocation )
    {
        super ( parent, initialLocation, "Entry", "Entry set-point value" );
        _output = output;
    }

    @Override
    protected Control createDialogArea ( Composite parent )
    {
        Composite buttonArea = new Composite ( parent, SWT.NONE );

        RowLayout layout = new RowLayout ();
        layout.type = SWT.HORIZONTAL;
        buttonArea.setLayout ( layout );

        // Text Entry
        _textEntry = new Text ( buttonArea, SWT.NONE );
        _textEntry.setLayoutData ( new RowData ( 100, 20 ) );

        // Ok Button
        Button okButton = new Button ( buttonArea, SWT.NONE );
        okButton.setText ( "OK" );
        okButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                perform ();
            }
        } );

        // cancel button
        Button cancelButton = new Button ( buttonArea, SWT.NONE );
        cancelButton.setText ( "Cancel" );
        cancelButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                close ();
            }
        } );

        return buttonArea;
    }

    protected void perform ()
    {
        String value = _textEntry.getText ();
        _output.setValue ( new Variant ( value ) );
        close ();
    }

}
