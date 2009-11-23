/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.dataitem.details.part.overview;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openscada.da.client.dataitem.details.part.AbstractBaseDetailsPart;
import org.openscada.da.client.dataitem.details.part.DetailsPart;
import org.openscada.da.ui.connection.data.DataItemHolder;

public class OverviewDetailsPart extends AbstractBaseDetailsPart implements DetailsPart
{

    private Text connectionUriText;

    private Text itemIdText;

    private Text stateText;

    private Text alarmText;

    private Text errorText;

    private Text valueText;

    private Text timestampText;

    public void createPart ( final Composite parent )
    {
        parent.setLayout ( new GridLayout ( 2, false ) );

        Label label;

        // connection uri
        label = new Label ( parent, SWT.NONE );
        label.setText ( "Connection: " );
        this.connectionUriText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.connectionUriText.setVisible ( false ); // FIXME: for now
        this.connectionUriText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        // item id
        label = new Label ( parent, SWT.NONE );
        label.setText ( "Item ID: " );
        this.itemIdText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.itemIdText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        // item state
        label = new Label ( parent, SWT.NONE );
        label.setText ( "Subscription State:" );
        this.stateText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.stateText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        label = new Label ( parent, SWT.NONE );
        label.setText ( "Alarm:" );
        this.alarmText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.alarmText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        label = new Label ( parent, SWT.NONE );
        label.setText ( "Error:" );
        this.errorText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.errorText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        label = new Label ( parent, SWT.NONE );
        label.setText ( "Value:" );
        this.valueText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.valueText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        label = new Label ( parent, SWT.NONE );
        label.setText ( "Timestamp:" );
        this.timestampText = new Text ( parent, SWT.READ_ONLY | SWT.BORDER );
        this.timestampText.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );
    }

    @Override
    public void setDataItem ( final DataItemHolder item )
    {
        super.setDataItem ( item );

        if ( item != null )
        {
            this.connectionUriText.setText ( item.getItem ().getConnectionString () );
            this.itemIdText.setText ( item.getItem ().getId () );
        }
        else
        {
            this.connectionUriText.setText ( "" );
            this.itemIdText.setText ( "" );
            this.stateText.setText ( "" );
            this.alarmText.setText ( "" );
            this.errorText.setText ( "" );
            this.valueText.setText ( "" );
            this.timestampText.setText ( "" );
        }
    }

    @Override
    protected void update ()
    {
        if ( this.value == null )
        {
            return;
        }

        if ( this.value.getSubscriptionError () == null )
        {
            this.stateText.setText ( this.value.getSubscriptionState ().name () );
        }
        else
        {
            this.stateText.setText ( String.format ( "%s (%s)", this.value.getSubscriptionState ().name (), this.value.getSubscriptionError ().getMessage () ) );
        }

        this.alarmText.setText ( isAlarm () ? "alarm active" : "no alarm" );
        this.errorText.setText ( isError () ? "error" : "ok" );

        this.valueText.setText ( this.value.getValue () != null ? this.value.getValue ().toString () : "<null>" );
        final Calendar c = this.value.getTimestamp ();
        this.timestampText.setText ( c != null ? String.format ( "%1$tF %1$tT,%1$tL", c ) : "<null>" );
    }
}
