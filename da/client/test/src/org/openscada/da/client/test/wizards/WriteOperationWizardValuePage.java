/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.client.test.views.realtime.ListEntry;
import org.openscada.rcp.da.client.browser.DataItemEntry;
import org.openscada.rcp.da.client.browser.HiveConnection;
import org.openscada.rcp.da.client.browser.ValueType;

class WriteOperationWizardValuePage extends WizardPage implements IWizardPage
{
    private static Logger _log = Logger.getLogger ( WriteOperationWizardValuePage.class );

    private Text _itemNameText = null;

    private Text _valueText = null;

    private Combo _valueTypeSelect = null;

    private IStructuredSelection _selection = null;

    private Text _convertedValue = null;

    private Color _defaultValueColor = null;

    private HiveConnection _connection = null;

    private Variant _value = null;

    protected WriteOperationWizardValuePage ()
    {
        super ( "wizardPage" );
        setTitle ( "Write Data Item" );
        setDescription ( "Enter the information to write" );
    }

    public void createControl ( final Composite parent )
    {
        final Composite container = new Composite ( parent, SWT.NONE );

        final GridLayout layout = new GridLayout ();
        container.setLayout ( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        Label label = new Label ( container, SWT.NONE );
        label.setText ( "&Item:" );

        this._itemNameText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._itemNameText.setLayoutData ( gd );
        this._itemNameText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                dialogChanged ();
            }
        } );

        label = new Label ( container, SWT.NONE );

        // row 2
        label = new Label ( container, SWT.NONE );
        label.setText ( "&Value:" );
        label.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.BEGINNING, false, false ) );

        this._valueText = new Text ( container, SWT.BORDER | SWT.MULTI );
        gd = new GridData ( SWT.FILL, SWT.FILL, true, true );
        this._valueText.setLayoutData ( gd );
        this._valueText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                dialogChanged ();
            }
        } );

        this._valueTypeSelect = new Combo ( container, SWT.DROP_DOWN | SWT.READ_ONLY );
        for ( final ValueType vt : ValueType.values () )
        {
            this._valueTypeSelect.add ( vt.label (), vt.ordinal () );
        }
        this._valueTypeSelect.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                dialogChanged ();
            }
        } );
        this._valueTypeSelect.select ( ValueType.STRING.ordinal () );
        this._valueTypeSelect.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.BEGINNING, false, false ) );

        // row 3

        label = new Label ( container, SWT.NONE );
        label.setText ( "Converted Value: " );

        this._convertedValue = new Text ( container, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._convertedValue.setLayoutData ( gd );
        this._defaultValueColor = this._convertedValue.getForeground ();

        setControl ( container );
        fillFromSelection ();
        dialogChanged ();
    }

    private void fillFromSelection ()
    {
        if ( this._selection == null )
        {
            return;
        }

        final Object obj = this._selection.getFirstElement ();
        if ( obj == null )
        {
            return;
        }
        if ( obj instanceof DataItemEntry )
        {
            this._itemNameText.setText ( ( (DataItemEntry)obj ).getId () );
        }
        else if ( obj instanceof ListEntry )
        {
            this._itemNameText.setText ( ( (ListEntry)obj ).getDataItem ().getItemId () );
        }
    }

    private void setValueText ( final String value, final boolean systemText )
    {
        this._convertedValue.setText ( value );

        if ( systemText )
        {
            final Color color = this._convertedValue.getDisplay ().getSystemColor ( SWT.COLOR_RED );
            this._convertedValue.setForeground ( color );
        }
        else
        {
            this._convertedValue.setForeground ( this._defaultValueColor );
        }
    }

    private void dialogChanged ()
    {
        // connection
        if ( this._connection == null )
        {
            updateStatus ( "No hive connection selection" );
            return;
        }

        // item
        if ( this._itemNameText.getText ().length () <= 0 )
        {
            updateStatus ( "Item name must not be empty" );
            return;
        }

        // value stuff
        setValueText ( "<not set>", true );
        this._value = null;

        final int idx = this._valueTypeSelect.getSelectionIndex ();
        try
        {
            for ( final ValueType vt : ValueType.values () )
            {
                if ( vt.ordinal () == idx )
                {
                    this._value = vt.convertTo ( this._valueText.getText () );
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
            _log.error ( "Failed to convert", e );
        }
        if ( this._value != null )
        {
            try
            {
                setValueText ( this._value.asString (), false );
            }
            catch ( final NullValueException e )
            {
                setValueText ( "<null>", true );
            }
        }
        else
        {
            setValueText ( "no converter found for: " + idx, true );
        }

        updateStatus ( null );
    }

    private void updateStatus ( final String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }

    public String getItem ()
    {
        return this._itemNameText.getText ();
    }

    public Variant getValue ()
    {
        return this._value;
    }

    public HiveConnection getConnection ()
    {
        return this._connection;
    }

    public void setSelection ( final IStructuredSelection selection )
    {
        this._selection = selection;

        final Object obj = this._selection.getFirstElement ();
        if ( obj instanceof HiveConnection )
        {
            this._connection = (HiveConnection)obj;
        }
        else if ( obj instanceof DataItemEntry )
        {
            this._connection = ( (DataItemEntry)obj ).getConnection ();
        }
        else if ( obj instanceof ListEntry )
        {
            this._connection = ( (ListEntry)obj ).getConnection ();
        }
    }
}