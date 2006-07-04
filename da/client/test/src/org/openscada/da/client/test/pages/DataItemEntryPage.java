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

package org.openscada.da.client.test.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.openscada.da.client.test.impl.DataItemEntry;

public class DataItemEntryPage extends PropertyPage implements
        IWorkbenchPropertyPage
{
    private Label _label = null;

    public DataItemEntryPage ()
    {
    }

    @Override
    protected Control createContents ( Composite parent )
    {
        _label = new Label ( parent, SWT.NONE );
        DataItemEntry item = getItem ();
        if ( item != null )
            _label.setText ( "Hello World: " + item.getId () );
        else
            _label.setText ( "Hello World: <no item>" );
        return _label;
    }

    private DataItemEntry getItem ()
    {
        if ( getElement () instanceof DataItemEntry )
            return (DataItemEntry)getElement ();
        else
            return null;
    }
    
    @Override
    protected void performDefaults ()
    {
        DataItemEntry item = getItem ();
        if ( item != null )
            _label.setText ( "Hello World: " + item.getId () );
        else
            _label.setText ( "Hello World: <no item>" );
        super.performDefaults ();
    }
}
