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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventAction;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Subscription;
import org.openscada.ae.storage.common.SubscriptionObserver;
import org.openscada.ae.storage.common.SubscriptionReader;

public class QueueSubscriptionReader implements SubscriptionReader, PushEventReader
{
    private static Logger _log = Logger.getLogger ( QueueSubscriptionReader.class );
    
    private ListeningQuery _query = null;
    
    private InitialEventsProvider _eventsProvider = null;
    private List<EventInformation> _events = null;
    
    private Subscription _subscription = null;
    private SubscriptionObserver _subscriptionObserver = null;
    
    private int _archiveSet = 0;
    
    public QueueSubscriptionReader ( ListeningQuery query, InitialEventsProvider eventsProvider, int archiveSet )
    {
        _query = query;
        _eventsProvider = eventsProvider;
        _archiveSet = archiveSet;
    }

    synchronized public EventInformation[] fetchNext ( int maxBatchSize )
    {
        if ( !hasMoreElements () )
            return new EventInformation[0];
        
        int num = Math.min ( maxBatchSize, _events.size () );
        if ( maxBatchSize == 0 )
            num = _events.size ();
        
        EventInformation[] events = new EventInformation[num];
        
        Iterator<EventInformation> iter = _events.iterator ();
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

    synchronized public void open ( Subscription subscription, SubscriptionObserver observer )
    {
        _log.debug ( String.format ( "Opened: archiveSet: %1$d", _archiveSet ) );
        _subscription = subscription;
        _subscriptionObserver = observer;
        
        // check if subscribe wants archive entries
        if ( _archiveSet == 0 )
            return;
        
        // get initial events
        Event[] initial = _eventsProvider.getInitialEvents (); 
        if ( _archiveSet < 0 )
        {
            _archiveSet = initial.length;
        }
        
        // copy events into event pool
        _events = new LinkedList<EventInformation> ();
        
        for ( int i = initial.length - _archiveSet; i<initial.length; i++ )
        {
            _events.add ( new EventInformation ( initial[i], EventAction.ADDED ) );
        }
        
        _log.debug ( _events.size () + " initial event(s)" );
        if ( !_events.isEmpty () )
        {
            notifyChange ();
        }
    }

    synchronized public void close ()
    {
        if ( _query != null )
        {
            _query.notifyClose ( this );
            _query = null;
        }
        if ( _events != null )
        {
            _events.clear ();
            _events = null;
        }
    }
    
    protected void notifyChange ()
    {
        _subscriptionObserver.changed ( _subscription );
    }

    synchronized public void pushEvent ( EventInformation event )
    {
        if ( _events != null )
        {
            _events.add ( event );
            notifyChange ();
        }
    }

}
