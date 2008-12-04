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

package org.openscada.da.client.test.views.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.rcp.da.client.browser.HiveConnection;

public class ListEntry extends Observable implements Observer
{
    public class AttributePair
    {
        public String key;

        public Variant value;

        public AttributePair ( final String key, final Variant value )
        {
            super ();
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode ()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ( this.key == null ? 0 : this.key.hashCode () );
            result = PRIME * result + ( this.value == null ? 0 : this.value.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final AttributePair other = (AttributePair)obj;
            if ( this.key == null )
            {
                if ( other.key != null )
                {
                    return false;
                }
            }
            else if ( !this.key.equals ( other.key ) )
            {
                return false;
            }
            if ( this.value == null )
            {
                if ( other.value != null )
                {
                    return false;
                }
            }
            else if ( !this.value.equals ( other.value ) )
            {
                return false;
            }
            return true;
        }
    }

    private DataItem dataItem;

    private HiveConnection connection;

    public HiveConnection getConnection ()
    {
        return this.connection;
    }

    public DataItem getDataItem ()
    {
        return this.dataItem;
    }

    public synchronized void setDataItem ( final String itemId, final HiveConnection connection )
    {
        clear ();
        this.connection = connection;
        this.dataItem = new DataItem ( itemId, connection.getItemManager () );
        this.dataItem.addObserver ( this );
    }

    public synchronized void clear ()
    {
        this.connection = null;
        if ( this.dataItem != null )
        {
            this.dataItem.deleteObserver ( this );
            this.dataItem.unregister ();
        }
    }

    public Variant getValue ()
    {
        return this.dataItem.getSnapshotValue ().getValue ();
    }

    public SubscriptionState getSubscriptionChange ()
    {
        return this.dataItem.getSnapshotValue ().getSubscriptionState ();
    }

    public synchronized List<AttributePair> getAttributes ()
    {
        final DataItemValue value = this.dataItem.getSnapshotValue ();

        final List<AttributePair> pairs = new ArrayList<AttributePair> ( value.getAttributes ().size () );
        for ( final Map.Entry<String, Variant> entry : value.getAttributes ().entrySet () )
        {
            pairs.add ( new AttributePair ( entry.getKey (), entry.getValue () ) );
        }
        return pairs;
    }

    /**
     * check if attributes are in the list
     * @return <code>true</code> if the attributes list is not empty
     */
    public synchronized boolean hasAttributes ()
    {
        return !this.dataItem.getSnapshotValue ().getAttributes ().isEmpty ();
    }

    public Throwable getSubscriptionError ()
    {
        return this.dataItem.getSnapshotValue ().getSubscriptionError ();
    }

    public void update ( final Observable o, final Object arg )
    {
        setChanged ();
        notifyObservers ( arg );
    }
}
