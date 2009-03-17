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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.base.item.DataItemHolder;

public abstract class AbstractBaseDetailsPart implements Observer, DetailsPart
{

    protected Display display;

    protected DataItem item;

    protected DataItemValue value;

    protected DataItemHolder itemHolder;

    protected Shell shell;

    private Composite parent;

    public void createPart ( final Composite parent )
    {
        this.display = parent.getDisplay ();
        this.shell = parent.getShell ();
        this.parent = parent;
    }

    public void setDataItem ( final DataItemHolder itemHolder, final DataItem item )
    {
        this.itemHolder = itemHolder;

        if ( this.item != null )
        {
            this.item.deleteObserver ( this );
            this.item = null;
        }
        this.item = item;

        if ( this.item != null )
        {
            // fetch the initial value
            this.value = this.item.getSnapshotValue ();
            this.item.addObserver ( this );
            update ();
        }
    }

    public void dispose ()
    {
        if ( this.item != null )
        {
            this.item.deleteObserver ( this );
            this.item = null;
        }
    }

    /**
     * called by DataItem
     */
    public void update ( final Observable o, final Object arg )
    {
        AbstractBaseDetailsPart.this.value = AbstractBaseDetailsPart.this.item.getSnapshotValue ();

        // trigger async update in display thread
        this.display.asyncExec ( new Runnable () {

            public void run ()
            {
                if ( !AbstractBaseDetailsPart.this.parent.isDisposed () )
                {
                    AbstractBaseDetailsPart.this.update ();
                }
            }
        } );
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