package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CommandMessageDialog extends PopupDialog
{
    public CommandMessageDialog ( Shell parent, CommandInformation[] commands )
    {
        super ( parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, true, false, "Commands", "Choose a command" );
        _commands = commands;
    }

    private CommandInformation[] _commands = null;

    @Override
    protected Control createDialogArea ( Composite parent )
    {
        Composite buttonArea = new Composite ( parent, SWT.NONE );
        
        RowLayout layout = new RowLayout ();
        layout.type = SWT.VERTICAL;
        buttonArea.setLayout ( layout );

        for ( final CommandInformation ci : _commands )
        {
            Button b = new Button ( buttonArea, SWT.NONE );
            b.setText ( ci.getLabel () );
            b.addSelectionListener ( new SelectionAdapter () {
                @Override
                public void widgetSelected ( SelectionEvent e )
                {
                    clicked ( ci );
                }
            } );
        }
        
        Button cancelButton = new Button ( buttonArea, SWT.NONE );
        cancelButton.setText ( "Cancel" );
        cancelButton.addSelectionListener ( new SelectionAdapter () {@Override
        public void widgetSelected ( SelectionEvent e )
        {
            close ();
        }} );

        return buttonArea;
    }
    
    protected void clicked ( CommandInformation ci )
    {
        ci.getOutput ().setValue ( true );
        close ();
    }
}
