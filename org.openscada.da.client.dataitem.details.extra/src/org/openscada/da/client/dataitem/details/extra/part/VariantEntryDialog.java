package org.openscada.da.client.dataitem.details.extra.part;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.client.base.browser.ValueType;

public class VariantEntryDialog extends TitleAreaDialog
{
    private Variant value;

    private Text convertText;

    private Text valueText;

    private Combo valueTypeSelect;

    public VariantEntryDialog ( final Shell parentShell )
    {
        super ( parentShell );
        setBlockOnOpen ( true );
    }

    public Variant getValue ()
    {
        if ( this.open () != Dialog.OK )
        {
            return null;
        }
        return this.value;
    }

    @Override
    protected Control createDialogArea ( final Composite parent )
    {
        final Control control = super.createDialogArea ( parent );
        setMessage ( "Enter a value", IMessageProvider.INFORMATION );
        setTitle ( "Enter variant" );
        this.getShell ().setText ( "Enter variant" );
        createEntryArea ( (Composite)control );
        return control;
    }

    protected Control createEntryArea ( final Composite parent )
    {
        final Composite comp = new Composite ( parent, SWT.NONE );
        comp.setLayoutData ( new GridData ( GridData.FILL_BOTH ) );

        comp.setLayout ( new GridLayout ( 2, false ) );

        this.valueText = new Text ( comp, SWT.BORDER | SWT.MULTI );
        this.valueText.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
        this.valueText.addModifyListener ( new ModifyListener () {

            public void modifyText ( final ModifyEvent e )
            {
                dialogChanged ();
            }
        } );

        this.valueTypeSelect = new Combo ( comp, SWT.DROP_DOWN | SWT.READ_ONLY );
        for ( final ValueType vt : ValueType.values () )
        {
            this.valueTypeSelect.add ( vt.label (), vt.ordinal () );
        }
        this.valueTypeSelect.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                VariantEntryDialog.this.dialogChanged ();
            }
        } );
        this.valueTypeSelect.select ( ValueType.STRING.ordinal () );
        this.valueTypeSelect.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1 ) );

        this.convertText = new Text ( comp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY );
        this.convertText.setLayoutData ( new GridData ( GridData.FILL, GridData.FILL, true, true, 2, 1 ) );

        return comp;
    }

    protected void dialogChanged ()
    {
        // value stuff
        setValueText ( "<no value>", true );
        this.value = null;

        final int idx = this.valueTypeSelect.getSelectionIndex ();
        try
        {
            for ( final ValueType vt : ValueType.values () )
            {
                if ( vt.ordinal () == idx )
                {
                    this.value = vt.convertTo ( this.valueText.getText () );
                }
            }
        }
        catch ( final NotConvertableException e )
        {
            updateStatus ( "Unable to convert value to target type: " + e.getMessage () );
            return;
        }
        catch ( final Exception e )
        {
            // _log.error ( "Failed to convert", e );
        }
        if ( this.value != null )
        {
            setValueText ( this.value.toString (), false );
        }
        else
        {
            setValueText ( "no converter found for: " + idx, true );
        }

        updateStatus ( null );
    }

    private void setValueText ( final String stringValue, final boolean error )
    {
        this.convertText.setText ( stringValue );
    }

    private void updateStatus ( final String message )
    {
        setMessage ( message, IMessageProvider.ERROR );
    }
}
