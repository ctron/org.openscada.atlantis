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
import org.openscada.core.ConnectionInformation;
import org.openscada.rcp.da.client.connector.ConnectorHelper;
import org.openscada.rcp.da.client.connector.DriverAdapterInformation;

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
        if ( this._italicFont != null )
        {
            return this._italicFont;
        }

        final FontData[] fd = this._uriText.getFont ().getFontData ();
        for ( int i = 0; i < fd.length; i++ )
        {
            fd[i].setStyle ( SWT.ITALIC );
        }
        this._italicFont = new Font ( this._uriText.getDisplay (), fd );
        return this._italicFont;
    }

    protected Font getBaseFont ()
    {
        return this._baseFont;
    }

    @Override
    public void dispose ()
    {
        try
        {
            if ( this._italicFont != null )
            {
                this._italicFont.dispose ();
            }
        }
        catch ( final Exception e )
        {
            _log.warn ( "Failed to dispose italic font", e );
        }
        super.dispose ();
    }

    public void createControl ( final Composite parent )
    {
        final Composite container = new Composite ( parent, SWT.NULL );

        final GridLayout layout = new GridLayout ();
        container.setLayout ( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        // row #1
        Label label = new Label ( container, SWT.NULL );
        label.setText ( "&URI:" );

        this._uriText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        this._baseFont = this._uriText.getFont ();
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._uriText.setLayoutData ( gd );
        this._uriText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                uriChanged ();
            }
        } );

        label = new Label ( container, SWT.NULL );

        // row #2
        label = new Label ( container, SWT.NULL );
        label.setText ( "Interface:" );

        this._interfaceText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._interfaceText.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // row #3
        label = new Label ( container, SWT.NULL );
        label.setText ( "Driver:" );

        this._driverText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._driverText.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // row #4
        label = new Label ( container, SWT.NULL );
        label.setText ( "Target:" );

        this._targetText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._targetText.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // row #5
        label = new Label ( container, SWT.NULL );
        label.setText ( "Secondary Target:" );

        this._secondaryTargetText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._secondaryTargetText.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // row #6
        label = new Label ( container, SWT.NULL );
        label.setText ( "Sub-Targets:" );

        this._subTargetsList = new List ( container, SWT.BORDER | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        this._subTargetsList.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // row #7
        label = new Label ( container, SWT.NULL );
        label.setText ( "Properties:" );

        this._propertiesTable = new Table ( container, SWT.BORDER | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        this._propertiesTable.setLayoutData ( gd );
        TableColumn col;
        col = new TableColumn ( this._propertiesTable, SWT.NONE );
        col.setText ( "Key" );
        col.pack ();
        col = new TableColumn ( this._propertiesTable, SWT.NONE );
        col.setText ( "Value" );
        col.pack ();
        this._propertiesTable.setHeaderVisible ( true );

        label = new Label ( container, SWT.NULL );

        // row #5
        label = new Label ( container, SWT.NULL );
        label.setText ( "Connector:" );

        this._connectionClassText = new Text ( container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._connectionClassText.setLayoutData ( gd );

        label = new Label ( container, SWT.NULL );

        // set the content
        setControl ( container );

        this._uriText.setText ( "da:net://localhost:1202" );
        uriChanged ();
    }

    private void uriChanged ()
    {
        try
        {
            clearCI ();
            clearDI ();

            final String uri = this._uriText.getText ();

            if ( uri.length () <= 0 )
            {
                updateStatus ( "URI may not be empty" );
                return;
            }

            final ConnectionInformation ci = ConnectionInformation.fromURI ( uri );
            if ( !ci.isValid () )
            {
                updateStatus ( "URI is invalid" );
                return;
            }
            updateStatus ( null );
            setMessage ( null );
            updateCI ( ci );

            final DriverAdapterInformation adapterInformation = ConnectorHelper.findDriverAdapterInformation ( ci );

            if ( adapterInformation == null )
            {
                setMessage ( "No adapter found for connection", WizardPage.WARNING );
            }
            else
            {
                updateDI ( adapterInformation );
            }
        }
        catch ( final Throwable e )
        {
            updateStatus ( "URI invalid: " + e.getMessage () );
        }
    }

    private void clearCI ()
    {
        this._interfaceText.setText ( "" );
        this._driverText.setText ( "" );
        this._targetText.setText ( "" );
        this._secondaryTargetText.setText ( "" );
        this._subTargetsList.removeAll ();
        this._propertiesTable.removeAll ();
    }

    public void clearDI ()
    {
        this._connectionClassText.setText ( "" );
    }

    private void updateCI ( final ConnectionInformation ci )
    {
        this._interfaceText.setText ( ci.getInterface () );
        this._driverText.setText ( ci.getDriver () );
        this._targetText.setText ( ci.getTarget () );
        if ( ci.getSecondaryTarget () != null )
        {
            this._secondaryTargetText.setFont ( getBaseFont () );
            this._secondaryTargetText.setText ( ci.getSecondaryTarget ().toString () );
        }
        else
        {
            this._secondaryTargetText.setFont ( getItalicFont () );
            this._secondaryTargetText.setText ( "<null>" );
        }

        for ( final String subtarget : ci.getSubtargets () )
        {
            this._subTargetsList.add ( subtarget );
        }

        for ( final Map.Entry<String, String> entry : ci.getProperties ().entrySet () )
        {
            final TableItem ti = new TableItem ( this._propertiesTable, SWT.NONE );
            ti.setText ( new String[] { entry.getKey (), entry.getValue () } );
        }
    }

    private void updateDI ( final DriverAdapterInformation adapter )
    {
        this._connectionClassText.setText ( adapter.getName () );
    }

    private void updateStatus ( final String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }

    public String getConnectionString ()
    {
        return this._uriText.getText ();
    }
}