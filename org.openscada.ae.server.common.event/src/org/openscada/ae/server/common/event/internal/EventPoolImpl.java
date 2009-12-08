package org.openscada.ae.server.common.event.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.server.common.event.EventMatcher;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPoolImpl implements EventListener, EventQuery
{

    private final static Logger logger = LoggerFactory.getLogger ( EventPoolImpl.class );

    private final Queue<Event> events = new LinkedList<Event> ();

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    private final Storage storage;

    private final EventManager eventManager;

    private final String filter;

    private final EventMatcher matcher;

    private final int size;

    private final Executor executor;

    public EventPoolImpl ( final Executor executor, final Storage storage, final EventManager eventManager, final String filter )
    {
        this.storage = storage;
        this.eventManager = eventManager;
        this.filter = filter;
        this.matcher = new EventMatcherImpl(filter);
        this.size = 100;
        this.executor = executor;
    }

    public synchronized void start () throws Exception
    {
        // add to event manager
        this.eventManager.addEventListener ( this );

        // load initial set from storage
        final Query query = this.storage.query ( this.filter );
        try
        {
            final Collection<Event> result = query.getNext ( this.size );
            logger.debug ( "Loaded {} entries from storage", result.size () );
            this.events.addAll ( result );
            notifyEvent ( result.toArray ( new Event[result.size ()] ) );
        }
        finally
        {
            query.dispose ();
        }
    }

    public synchronized void stop ()
    {
        this.eventManager.removeEventListener ( this );
    }

    public synchronized void handleEvent ( final Event[] events )
    {
        Set<Event> toNotify = new HashSet<Event> ();
        for ( Event event : events )
        {
            if (matcher.matches ( event )) {
                toNotify.add ( event );
            }
        }
        this.events.addAll ( toNotify );
        while ( this.events.size () > this.size )
        {
            this.events.remove ();
        }
        notifyEvent ( toNotify.toArray (new Event[0]) );
    }

    private void notifyEvent ( final Event[] event )
    {
        final EventListener[] listeners = this.listeners.toArray ( new EventListener[this.listeners.size ()] );
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final EventListener listener : listeners )
                {
                    listener.handleEvent ( event );
                }
            }
        } );
    }

    public synchronized void addListener ( final EventListener eventListener )
    {
        this.listeners.add ( eventListener );

        // deliver pool events
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                eventListener.handleEvent ( EventPoolImpl.this.events.toArray ( new Event[EventPoolImpl.this.events.size ()] ) );
            }
        } );
    }

    public synchronized void removeListener ( final EventListener eventListener )
    {
        this.listeners.remove ( eventListener );
    }

}
