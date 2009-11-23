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

package org.openscada.da.client.dataitem.details.part;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.ui.connection.data.DataItemHolder;

public abstract class AbstractBaseDetailsPart implements DetailsPart
{
    protected Display display;

    protected DataItemHolder item;

    protected DataItemValue value;

    protected Shell shell;

    public void setDataItem ( final DataItemHolder item )
    {
        this.item = item;
    }

    public void dispose ()
    {
    }

    public void updateData ( final DataItemValue value )
    {
        this.value = value;
        update ();
    }

    protected abstract void update ();

    /**
     * Return if the value is unsafe
     * @return <code>true</code> if the value part is unsafe, <code>false</code> otherwise
     */
    protected boolean isUnsafe ()
    {
        return this.value.isError () || !this.value.isConnected ();
    }

    protected boolean isError ()
    {
        return this.value.isError ();
    }

    protected boolean isAlarm ()
    {
        return this.value.isAlarm ();
    }

    protected boolean isManual ()
    {
        return this.value.isManual ();
    }

    protected boolean getBooleanAttribute ( final String name )
    {
        if ( this.value.getAttributes ().containsKey ( name ) )
        {
            return this.value.getAttributes ().get ( name ).asBoolean ();
        }
        return false;
    }

    protected Number getNumberAttribute ( final String name, final Number defaultValue )
    {
        final Variant value = this.value.getAttributes ().get ( name );

        if ( value == null )
        {
            return defaultValue;
        }
        if ( value.isNull () )
        {
            return defaultValue;
        }

        try
        {
            if ( value.isDouble () )
            {
                return value.asDouble ();
            }
            if ( value.isInteger () )
            {
                return value.asInteger ();
            }
            if ( value.isLong () )
            {
                return value.asLong ();
            }
            if ( value.isBoolean () )
            {
                return value.asBoolean () ? 1 : 0;
            }
            return Double.parseDouble ( value.asString () );
        }
        catch ( final Throwable e )
        {
        }

        return defaultValue;
    }

}