package org.openscada.ae.client.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.Event;
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.EventListener;
import org.openscada.core.subscription.SubscriptionState;

public class EventSyncController implements EventListener
{
    private final List<EventListener> listeners = new CopyOnWriteArrayList<EventListener> ();

    private final Connection connection;

    private final String id;

    private final Set<Event> cachedEvents = Collections.<Event> newSetFromMap ( new ConcurrentHashMap<Event, Boolean> () );

    public EventSyncController ( final Connection connection, final String id )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;
        this.id = id;
        this.connection.setEventListener ( this.id, this );
    }

    public synchronized void addListener ( final EventListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( this.cachedEvents.toArray ( new Event[] {} ) );
    }

    /**
     * returns true if no listeners left
     * @param listener
     * @return
     */
    public synchronized boolean removeListener ( final EventListener listener )
    {
        this.listeners.remove ( listener );
        return ( this.listeners.size () == 0 );
    }

    public void dataChanged ( final Event[] addedEvents )
    {
        this.cachedEvents.removeAll ( Arrays.asList ( addedEvents ) );
        this.cachedEvents.addAll ( Arrays.asList ( addedEvents ) );
        for ( EventListener listener : this.listeners )
        {
            listener.dataChanged ( addedEvents );
        }
    }

    public void statusChanged ( final SubscriptionState state )
    {
        switch ( state )
        {
        case CONNECTED:
            for ( EventListener listener : this.listeners )
            {
                listener.dataChanged ( this.cachedEvents.toArray ( new Event[] {} ) );
            }
            break;
        default:
            break;
        }
    }

    public void dispose ()
    {
        this.connection.setEventListener ( this.id, null );
    }
}
