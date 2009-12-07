package org.openscada.ae.server.common.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.openscada.ae.Event;
import org.openscada.ae.server.ConditionListener;
import org.openscada.ae.server.EventListener;
import org.openscada.core.subscription.SubscriptionInformation;
import org.openscada.core.subscription.SubscriptionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventQuerySource implements SubscriptionSource, org.openscada.ae.event.EventListener
{

    private final static Logger logger = LoggerFactory.getLogger ( EventQuerySource.class );

    private final String id;

    private final EventQuery eventQuery;

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    private final Queue<Event> cachedData = new LinkedList<Event> ();

    public EventQuerySource ( final String id, final EventQuery query )
    {
        this.id = id;
        this.eventQuery = query;
    }

    public synchronized void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        final boolean wasEmpty = this.listeners.isEmpty ();

        for ( final SubscriptionInformation information : listeners )
        {
            final EventListener listener = (EventListener)information.getListener ();
            this.listeners.add ( listener );

            if ( !this.cachedData.isEmpty () )
            {
                listener.dataChanged ( this.id, this.cachedData.toArray ( new Event[this.cachedData.size ()] ) );
            }
        }

        if ( wasEmpty && !this.listeners.isEmpty () )
        {
            this.eventQuery.addListener ( this );
        }
    }

    public synchronized void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation information : listeners )
        {
            final ConditionListener listener = (ConditionListener)information.getListener ();
            this.listeners.remove ( listener );
        }

        if ( this.listeners.isEmpty () )
        {
            this.eventQuery.removeListener ( this );
            this.cachedData.clear ();
        }
    }

    public boolean supportsListener ( final SubscriptionInformation information )
    {
        return information.getListener () instanceof EventListener;
    }

    public synchronized void handleEvent ( final Event event )
    {
        this.cachedData.add ( event );

        for ( final EventListener listener : this.listeners )
        {
            try
            {
                listener.dataChanged ( this.id, new Event[] { event } );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to notify", e );
            }
        }
    }

}
