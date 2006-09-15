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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Query;
import org.openscada.ae.storage.common.Reader;
import org.openscada.ae.storage.common.SubscriptionReader;

public class MemoryQuery implements Query, ListeningQuery, InitialEventsProvider
{
    protected List<Event> _events = new LinkedList<Event> ();
    
    protected Set<PushEventReader> _subscriptionReaders = new HashSet<PushEventReader> ();
    
    synchronized public Reader createReader ()
    {
        return new CollectionReader ( new ArrayList<Event> ( _events ) );
    }

    synchronized public SubscriptionReader createSubscriptionReader ( int archiveSet )
    {
        QueueSubscriptionReader reader = new QueueSubscriptionReader ( this, this, archiveSet );
        _subscriptionReaders.add ( reader );
        return reader;
    }
    
    synchronized protected void fireEvent ( EventInformation eventInformation )
    {
        for ( PushEventReader reader : _subscriptionReaders )
        {
            reader.pushEvent ( eventInformation );
        }
    }

    synchronized public void submitEvent ( Event event )
    {
        EventInformation eventInformation = new EventInformation ( event, EventInformation.ACTION_ADDED );
        
        fireEvent ( eventInformation );
        _events.add ( event );
    }
    
    synchronized public void removeEvent ( Event event )
    {
        if ( _events.remove ( event ) )
        {
            EventInformation eventInformation = new EventInformation ( event, EventInformation.ACTION_REMOVED );
            fireEvent ( eventInformation );
        }
    }
    
    /* (non-Javadoc)
     * @see org.openscada.ae.storage.test.ListeningQuery#notifyClose(org.openscada.ae.storage.common.SubscriptionReader)
     */
    synchronized public void notifyClose ( SubscriptionReader reader )
    {
        _subscriptionReaders.remove ( reader );
    }

    synchronized public Event[] getInitialEvents ()
    {
        return _events.toArray ( new Event [ _events.size () ] );
    }

}
