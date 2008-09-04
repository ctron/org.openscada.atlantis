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
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.Connection;
import org.openscada.da.client.DataItem;

public abstract class AbstractBaseDetailsPart implements Observer, DetailsPart
{

    protected Display display;

    protected DataItem item;
    
    protected Connection connection;

    public AbstractBaseDetailsPart ()
    {
        super ();
    }

    public void createPart ( Composite parent )
    {
        display = parent.getDisplay ();

    }

    public void setDataItem ( Connection connection, DataItem item )
    {
        this.connection = connection;
        
        if ( this.item != null )
        {
            this.item.deleteObserver ( this );
        }
        this.item = item;

        if ( this.item != null )
        {
            this.item.addObserver ( this );
        }
    }

    public void dispose ()
    {
        if ( this.item != null )
        {
            this.item.deleteObserver ( this );
        }
    }

    /**
     * called by DataItem
     */
    public void update ( Observable o, Object arg )
    {
        // trigger async update in display thread
        this.display.asyncExec ( new Runnable () {

            public void run ()
            {
                if ( !display.isDisposed () )
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
        return getBooleanAttribute ( "error" ) || !this.item.getSubscriptionState ().equals ( SubscriptionState.CONNECTED );
    }
    
    protected boolean isAlarm ()
    {
        return getBooleanAttribute ( "alarm" );
    }
    
    protected boolean isManual ()
    {
        return getBooleanAttribute ( "org.openscada.da.manual.active" );
    }

    protected boolean getBooleanAttribute ( String name )
    {
        if ( item.getAttributes ().containsKey ( name ) )
        {
            return item.getAttributes ().get ( name ).asBoolean ();
        }
        return false;
    }

}