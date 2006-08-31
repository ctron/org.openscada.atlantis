package org.openscada.ae.storage.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Subscription;
import org.openscada.ae.storage.common.SubscriptionObserver;
import org.openscada.ae.storage.common.SubscriptionReader;

public class QueueSubscriptionReader implements SubscriptionReader, PushEventReader
{
    private ListeningQuery _query = null;
    
    private InitialEventsProvider _eventsProvider = null;
    private List<EventInformation> _events = null;
    
    private Subscription _subscription = null;
    private SubscriptionObserver _subscriptionObserver = null;
    
    public QueueSubscriptionReader ( ListeningQuery query, InitialEventsProvider eventsProvider )
    {
        _query = query;
        _eventsProvider = eventsProvider;
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
        _subscription = subscription;
        _subscriptionObserver = observer;
        
        // copy events into event pool
        _events = new LinkedList<EventInformation> ();
        for ( Event event : _eventsProvider.getInitialEvents () )
        {
            _events.add ( new EventInformation ( event, EventInformation.ACTION_ADDED ) );
        }
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
        }
    }

}
