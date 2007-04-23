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

package org.openscada.ae.storage.common.test;

import java.util.Iterator;
import java.util.List;

import org.openscada.ae.core.Event;
import org.openscada.ae.storage.common.Reader;

public class MockReader implements Reader
{

    private List<Event> _events = null;
    
    public Event[] fetchNext ( int maxBatchSize )
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

    public boolean hasMoreElements ()
    {
        if ( _events == null )
            return false;
        return _events.size () > 0;
    }

    public void close ()
    {
        _events = null;
    }

    public void setInitialEvents ( List<Event> events )
    {
        _events = events;
    }
}
