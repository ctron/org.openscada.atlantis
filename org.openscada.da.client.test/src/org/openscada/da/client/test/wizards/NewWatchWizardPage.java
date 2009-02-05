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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class NewWatchWizardPage extends WizardPage implements IWizardPage
{

    private Text _dataItemID = null;
    private String _dataItemIdText = null;

    protected NewWatchWizardPage (  )
    {
        super ( "wizardPage" );
        setTitle ( "Watch Data Item" );
        setDescription ( "Select the data item to watch" );
    }

    public void createControl ( Composite parent )
    {
        Composite container = new Composite ( parent, SWT.NULL );
        
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
       
        Label label;
        label = new Label ( container, SWT.NULL );
        label.setText ( "&Item ID:" );

        GridData gd;
        _dataItemID = new Text ( container, SWT.BORDER | SWT.SINGLE );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        _dataItemID.setLayoutData ( gd );
        _dataItemID.addModifyListener ( new ModifyListener() {
            public void modifyText ( ModifyEvent e ) {
                dialogChanged ();
            }
        });
        if ( _dataItemIdText != null )
            _dataItemID.setText ( _dataItemIdText );
        
        setControl ( container );
        
        dialogChanged ();
    }
    
    private void dialogChanged ()
    {
        if ( _dataItemID.getText ().length () <= 0 )
        {
            updateStatus ( "You need to enter an item ID" );
            return;
        }
        updateStatus ( null );
    }

    private void updateStatus ( String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }
    
    public String getDataItemID ()
    {
        return _dataItemID.getText ();
    }

    public void setDataItemId ( String id )
    {
        _dataItemIdText  = id;
    }

}