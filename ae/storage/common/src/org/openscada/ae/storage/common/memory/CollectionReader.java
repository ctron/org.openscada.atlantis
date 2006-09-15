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

package org.openscada.ae.storage.common.memory;

import java.util.Collection;
import java.util.Iterator;

import org.openscada.ae.core.Event;
import org.openscada.ae.storage.common.Reader;

public class CollectionReader implements Reader
{
    private Collection<Event> _events = null;
    
    public CollectionReader ( Collection<Event> events )
    {
        _events = events;
    }
    
    synchronized public Event[] fetchNext ( int maxBatchSize )
    {
        if ( !hasMoreElements () )
            return new Event[0];
        
        int num = Math.min ( maxBatchSize, _events.size () );
        if ( maxBatchSize == 0 )
            num = _events.size ();
        
        Event[] events = new Event[num];
        
        Iterator<Event> iter = _events.iterator ();
        for ( int i = 0; i < num; i++ )
        {
            events[i] = iter.next ();
            iter.remove ();
        }
        return events;
    }

    synchronized public boolean hasMoreElements ()
    {
        if ( _events == null )
            return false;
        return !_events.isEmpty ();
    }

    synchronized public void close ()
    {
        if ( _events != null )
        {
            _events.clear ();
            _events = null;
        }
    }

}
