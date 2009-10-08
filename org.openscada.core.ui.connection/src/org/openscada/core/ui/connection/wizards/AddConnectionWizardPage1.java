package org.openscada.core.ui.connection.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.ConnectionInformation;

public class AddConnectionWizardPage1 extends WizardPage
{

    private Text uriText;

    private ConnectionInformation connectionInformation;

    protected AddConnectionWizardPage1 ()
    {
        super ( "Add Connection" );
        setTitle ( "Add connection" );
        setDescription ( "Add a new connection to the connection store" );
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public void createControl ( final Composite parent )
    {
        final Composite comp = new Composite ( parent, SWT.NONE );
        comp.setLayout ( new GridLayout ( 2, false ) );

        Label label;
        label = new Label ( comp, SWT.NONE );
        label.setText ( "Connection URI:" );
        label.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.CENTER, false, false ) );

        this.uriText = new Text ( comp, SWT.BORDER );
        this.uriText.setMessage ( "Enter the connection uri" );
        this.uriText.setText ( "da:net://localhost:1202" );
        this.uriText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );
        this.uriText.addModifyListener ( new ModifyListener () {

            public void modifyText ( final ModifyEvent e )
            {
                update ();
            }
        } );

        setControl ( comp );
        update ();
    }

    public void update ()
    {
        String errorMessage = null;

        try
        {
            this.connectionInformation = ConnectionInformation.fromURI ( this.uriText.getText () );
        }
        catch ( final Throwable e )
        {
            errorMessage = e.getLocalizedMessage ();
        }
        setState ( errorMessage );
    }

    private void setState ( final String errorMessage )
    {
        if ( errorMessage == null )
        {
            setPageComplete ( true );
            setMessage ( "Add a connection to the connection store", INFORMATION );
        }
        else
        {
            setPageComplete ( false );
            setMessage ( errorMessage, ERROR );
        }
    }

}
