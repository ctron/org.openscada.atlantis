package org.openscada.ae.server.common.event.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class EventPoolImpl implements EventListener, EventQuery
{
    private final BundleContext context;

    private final SingleServiceTracker eventManagerTracker;

    private EventManager manager;

    private final Queue<Event> events = new LinkedList<Event> ();

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    public EventPoolImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
        this.eventManagerTracker = new SingleServiceTracker ( this.context, FilterUtil.createClassFilter ( EventManager.class.getName () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                EventPoolImpl.this.setEventManager ( (EventManager)service );
            }
        } );
        this.eventManagerTracker.open ();
    }

    protected void setEventManager ( final EventManager service )
    {
        if ( this.manager != null )
        {
            this.manager.removeEventListener ( this );
        }
        this.manager = service;
        if ( this.manager != null )
        {
            this.manager.addEventListener ( this );
        }
    }

    public void dispose ()
    {
        this.eventManagerTracker.close ();
    }

    public synchronized void handleEvent ( final Event event )
    {
        this.events.add ( event );
        while ( this.events.size () > 100 )
        {
            this.events.remove ();
        }
        for ( final EventListener listener : this.listeners )
        {
            listener.handleEvent ( event );
        }
    }

    public void addListener ( final EventListener eventListener )
    {
        this.listeners.add ( eventListener );
    }

    public void removeListener ( final EventListener eventListener )
    {
        this.listeners.remove ( eventListener );
    }

}
