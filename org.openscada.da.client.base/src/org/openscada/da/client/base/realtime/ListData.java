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

package org.openscada.da.client.base.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.openscada.da.ui.connection.data.Item;

public class ListData implements Observer
{
    private static Logger _log = Logger.getLogger ( ListData.class );

    private List<ListEntry> _items = new CopyOnWriteArrayList<ListEntry> ();

    private final Set<Listener> _listeners = new CopyOnWriteArraySet<Listener> ();

    public List<ListEntry> getItems ()
    {
        return new ArrayList<ListEntry> ( this._items );
    }

    synchronized public void setItems ( final List<ListEntry> items )
    {
        clear ();

        this._items = items;
        fireAdded ( this._items.toArray ( new ListEntry[this._items.size ()] ) );
        for ( final ListEntry entry : items )
        {
            entry.addObserver ( this );
        }
    }

    public void add ( final ListEntry entry )
    {
        if ( this._items.add ( entry ) )
        {
            fireAdded ( new ListEntry[] { entry } );
            entry.addObserver ( this );
        }
    }

    public void add ( final Item item )
    {
        final ListEntry entry = new ListEntry ();
        entry.setDataItem ( item );

        add ( entry );
    }

    public void remove ( final ListEntry entry )
    {
        if ( this._items.remove ( entry ) )
        {
            entry.deleteObserver ( this );
            entry.clear ();
            fireRemoved ( new ListEntry[] { entry } );
        }
    }

    synchronized public void clear ()
    {
        for ( final ListEntry entry : this._items )
        {
            entry.deleteObserver ( this );
        }
        this._items.clear ();
        fireRemoved ( this._items.toArray ( new ListEntry[this._items.size ()] ) );
    }

    public void addListener ( final Listener listener )
    {
        this._listeners.add ( listener );

        // now fill the new listener with what we already have
        if ( !this._items.isEmpty () )
        {
            listener.added ( this._items.toArray ( new ListEntry[this._items.size ()] ) );
        }
    }

    public void removeListener ( final Listener listener )
    {
        this._listeners.remove ( listener );
    }

    protected void fireAdded ( final ListEntry[] entries )
    {
        _log.debug ( String.format ( "Fire add for %d items", entries.length ) ); //$NON-NLS-1$
        for ( final Listener listener : this._listeners )
        {
            try
            {
                listener.added ( entries );
            }
            catch ( final Exception e )
            {
                _log.warn ( "Failed while sending add notification", e ); //$NON-NLS-1$
            }
        }
    }

    protected void fireRemoved ( final ListEntry[] entries )
    {
        for ( final Listener listener : this._listeners )
        {
            try
            {
                listener.removed ( entries );
            }
            catch ( final Exception e )
            {
                _log.warn ( "Failed while sending remove notification", e ); //$NON-NLS-1$
            }
        }
    }

    protected void fireUpdated ( final ListEntry[] entries )
    {
        _log.debug ( "Updating items: " + entries.length ); //$NON-NLS-1$

        for ( final Listener listener : this._listeners )
        {
            try
            {
                listener.updated ( entries );
            }
            catch ( final Exception e )
            {
                _log.warn ( "Failed while sending update notification", e ); //$NON-NLS-1$
            }
        }
    }

    public void update ( final Observable o, final Object arg )
    {
        if ( o instanceof ListEntry && this._items.contains ( o ) )
        {
            fireUpdated ( new ListEntry[] { (ListEntry)o } );
        }
    }
}
