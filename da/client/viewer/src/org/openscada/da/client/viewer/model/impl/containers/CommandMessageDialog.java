/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client.viewer.model.impl.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CommandMessageDialog extends PopupCommandDialog
{
    public CommandMessageDialog ( Shell parent, Point initialLocation, CommandInformation[] commands )
    {
        super ( parent, initialLocation, "Commands", "Choose a command" );
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
