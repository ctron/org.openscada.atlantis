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

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.core.client.DriverInformation;

class NewHiveWizardConnectionPage extends WizardPage implements IWizardPage
{
    private static Logger _log = Logger.getLogger ( NewHiveWizardConnectionPage.class );
    
    private Text _uriText;
    private Text _interfaceText;
    private Text _driverText;
    private Text _targetText;
    private Text _secondaryTargetText;
    
    private Font _baseFont;
    private Font _italicFont;
    private List _subTargetsList;

    private Table _propertiesTable;

    private Text _connectionClassText;

    protected NewHiveWizardConnectionPage ()
    {
        super ( "wizardPage" );
        setTitle ( "Connection information" );
        setDescription ( "Enter the connection information" );
    }
    
    protected Font getItalicFont ()
    {
        if ( _italicFont != null )
            return _italicFont;
        
        FontData [] fd = _uriText.getFont ().getFontData ();
        for ( int i = 0; i < fd.length; i++ )
        {
            fd[i].setStyle ( SWT.ITALIC );
        }
        _italicFont = new Font ( _uriText.getDisplay (), fd );
        return _italicFont;
    }
    
    protected Font getBaseFont ()
    {
        return _baseFont;
    }
    
    @Override
    public void dispose ()
    {
        try
        {
            if ( _italicFont != null )
            {
                _italicFont.dispose ();
            }
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to dispose italic font", e );
        }
        super.dispose ();
    }

    public void createControl ( Composite parent )
    {
        Composite container = new Composite ( parent, SWT.NULL );

        GridLayout layout = new GridLayout ();
        container.setLayout ( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        // row #1
        Label label = new Label ( container, SWT.NULL );
        label.setText ( "&URI:" );

        _uriText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        _baseFont = _uriText.getFont ();
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        _uriText.setLayoutData ( gd );
        _uriText.addModifyListener ( new ModifyListener () {
            public void modifyText ( ModifyEvent e )
            {
                uriChanged ();
            }
        } );
        
        label = new Label ( container, SWT.NULL );
        
        // row #2
        label = new Label ( container, SWT.NULL );
        label.setText ( "Interface:" );
        
        _interfaceText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _interfaceText.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );
        
        // row #3
        label = new Label ( container, SWT.NULL );
        label.setText ( "Driver:" );
        
        _driverText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _driverText.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );
        
        // row #4
        label = new Label ( container, SWT.NULL );
        label.setText ( "Target:" );
        
        _targetText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _targetText.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );
        
        // row #5
        label = new Label ( container, SWT.NULL );
        label.setText ( "Secondary Target:" );
        
        _secondaryTargetText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _secondaryTargetText.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );
        
        // row #6
        label = new Label ( container, SWT.NULL );
        label.setText ( "Sub-Targets:" );
        
        _subTargetsList = new List ( container, SWT.BORDER | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        _subTargetsList.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );
        
        // row #7
        label = new Label ( container, SWT.NULL );
        label.setText ( "Properties:" );
        
        _propertiesTable = new Table ( container, SWT.BORDER | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        _propertiesTable.setLayoutData ( gd );
        TableColumn col;
        col = new TableColumn ( _propertiesTable, SWT.NONE );
        col.setText ( "Key" );
        col.pack ();
        col = new TableColumn ( _propertiesTable, SWT.NONE );
        col.setText ( "Value" );
        col.pack ();
        _propertiesTable.setHeaderVisible ( true );
        
        label = new Label ( container, SWT.NULL );
        
        // row #5
        label = new Label ( container, SWT.NULL );
        label.setText ( "Connection Class:" );
        
        _connectionClassText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _connectionClassText.setLayoutData ( gd );
        
        label = new Label ( container, SWT.NULL );

        // set the content
        setControl ( container );

        _uriText.setText ( "da:net://localhost:1202" );
        uriChanged ();
    }

    private void uriChanged ()
    {
        try
        {
            clearCI ();
            clearDI ();
            
            String uri = _uriText.getText ();
            
            if ( uri.length () <= 0 )
            {
                updateStatus ( "URI may not be empty" );
                return;
            }
            
            ConnectionInformation ci = ConnectionInformation.fromURI ( uri );
            if ( !ci.isValid () )
            {
                updateStatus ( "URI is invalid" );
                return;
            }
            updateStatus ( null );
            setMessage ( null );
            updateCI ( ci );
            
            DriverInformation di = ConnectionFactory.findDriver ( ci );
            if ( di == null )
            {
                setMessage ( "No driver found for connection", WizardPage.WARNING );
            }
            else
            {
                updateDI ( di );
                try
                {
                    di.validate ( ci );
                }
                catch ( Throwable e )
                {
                    updateStatus ( String.format ( "Driver failed to validate connection: %s", e.getMessage () ) );
                }
            }
        }
        catch ( Throwable e )
        {
            updateStatus ( "URI invalid: " + e.getMessage () );
        }
    }

    private void clearCI ()
    {
        _interfaceText.setText ( "" );
        _driverText.setText ( "" );
        _targetText.setText ( "" );
        _secondaryTargetText.setText ( "" );
        _subTargetsList.removeAll ();
        _propertiesTable.removeAll ();
    }
    
    public void clearDI ()
    {
        _connectionClassText.setText ( "" );
    }
    
    private void updateCI ( ConnectionInformation ci )
    {
        _interfaceText.setText ( ci.getInterface () );
        _driverText.setText ( ci.getDriver () );
        _targetText.setText ( ci.getTarget () );
        if ( ci.getSecondaryTarget () != null )
        {
            _secondaryTargetText.setFont ( getBaseFont () );
            _secondaryTargetText.setText ( ci.getSecondaryTarget ().toString () );
        }
        else
        {
            _secondaryTargetText.setFont ( getItalicFont () );
            _secondaryTargetText.setText ( "<null>" );
        }
        
        for ( String subtarget : ci.getSubtargets () )
        {
            _subTargetsList.add ( subtarget );
        }
        
        for ( Map.Entry<String,String> entry : ci.getProperties ().entrySet () )
        {
            TableItem ti = new TableItem ( _propertiesTable, SWT.NONE );
            ti.setText ( new String [] { entry.getKey (), entry.getValue () } );
        }
    }
    
    private void updateDI ( DriverInformation di )
    {
        _connectionClassText.setText ( di.getConnectionClass ().toString () );
    }

    private void updateStatus ( String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }

    public String getConnectionString ()
    {
        return _uriText.getText ();
    }
}