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
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.VariantHelper.ValueType;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

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

    public void createControl ( Composite parent )
    {
        Composite container = new Composite ( parent, SWT.NONE );
        
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        
        
        Label label = new Label ( container, SWT.NONE );
        label.setText("&Item:");

        _itemNameText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        _itemNameText.setLayoutData ( gd );
        _itemNameText.addModifyListener ( new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                dialogChanged();
            }
        });
        
        label = new Label ( container, SWT.NONE );
       
        // row 2
        
        label = new Label(container, SWT.NONE );
        label.setText("&Value:");

        _valueText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        _valueText.setLayoutData(gd);
        _valueText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        _valueTypeSelect = new Combo ( container, SWT.DROP_DOWN );
        for ( ValueType vt : ValueType.values () )
        {
            _valueTypeSelect.add ( vt.label (), vt.index() );   
        }
        _valueTypeSelect.addSelectionListener ( new SelectionAdapter() {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                dialogChanged ();
            }
        } );
        _valueTypeSelect.select ( ValueType.STRING.index () );
        
        // row 3
        
        label = new Label ( container, SWT.NONE );
        label.setText ( "Converted Value: ");
        
        _convertedValue = new Text ( container, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
        gd = new GridData(GridData.FILL_HORIZONTAL);
        _convertedValue.setLayoutData(gd);
        _defaultValueColor = _convertedValue.getForeground ();

        setControl ( container );
        fillFromSelection ();
        dialogChanged ();
    }

    private void fillFromSelection ()
    {
        if ( _selection == null )
            return;
        
        Object obj = _selection.getFirstElement ();
        if ( obj == null )
            return;
        if ( !(obj instanceof DataItemEntry) )
            return;
        
        _itemNameText.setText ( ((DataItemEntry)obj).getId () );
    }
    
    private void setValueText ( String value, boolean systemText )
    {
        _convertedValue.setText ( value );
        
        if ( systemText )
        {
            Color color = _convertedValue.getDisplay ().getSystemColor ( SWT.COLOR_RED );
            _convertedValue.setForeground ( color );
        }
        else
        {
            _convertedValue.setForeground ( _defaultValueColor );
        }
    }
    
    private void dialogChanged ()
    {
        // connection
        if ( _connection == null )
        {
            updateStatus ( "No hive connection selection" );
            return;
        }
        
        // item
        if ( _itemNameText.getText ().length () <= 0 )
        {
            updateStatus ( "Item name must not be empty" );
            return;
        }

        // value stuff
        setValueText ( "<not set>", true );
        _value = null;
        
        int idx = _valueTypeSelect.getSelectionIndex ();
        try
        {
            for ( ValueType vt : ValueType.values () )
            {
                if ( vt.index () == idx )
                {
                    _value = vt.convertTo ( _valueText.getText() );
                }
            }
        }
        catch ( NotConvertableException e )
        {
            updateStatus ( "Unable to convert value to target type: " + e.getMessage () );
            return;
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to convert", e );
        }
        if ( _value != null )
        {
            try
            {
                setValueText ( _value.asString (), false );
            }
            catch ( NullValueException e )
            {
                setValueText ( "<null>", true );
            }
        }
        else
            setValueText ( "no converter found for: " + idx, true );

        updateStatus ( null );
    }

    private void updateStatus ( String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }
    
    public String getItem()
    {
        return _itemNameText.getText ();
    }
    
    public Variant getValue ()
    {
        return _value;
    }
    
    public HiveConnection getConnection()
    {
        return _connection;
    }

    public void setSelection ( IStructuredSelection selection )
    {
        _selection = selection;
        
        Object obj = _selection.getFirstElement ();
        if ( obj instanceof HiveConnection )
            _connection = (HiveConnection)obj;
        else if ( obj instanceof DataItemEntry )
            _connection = ((DataItemEntry)obj).getConnection ();
    }
}