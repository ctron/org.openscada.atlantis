package org.openscada.ae.event.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openscada.ae.Event;
import org.openscada.ae.event.EventListener;
import org.openscada.ae.event.EventManager;
import org.openscada.ae.event.EventService;
import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class EventServiceImpl implements EventService, EventManager
{

    private final BundleContext context;

    private final SingleServiceTracker storageTracker;

    private final List<Event> writeQueue = new LinkedList<Event> ();

    private final Set<EventListener> listeners = new HashSet<EventListener> ();

    private Storage storage;

    public EventServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
        final Filter filter = FilterUtil.createClassFilter ( Storage.class.getName () );
        this.storageTracker = new SingleServiceTracker ( this.context, filter, new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                setStorage ( (Storage)service );
            }
        } );
        this.storageTracker.open ();
    }

    public void dispose ()
    {
        this.storageTracker.close ();
    }

    protected synchronized void setStorage ( final Storage service )
    {
        this.storage = service;
    }

    public synchronized void publishEvent ( final Event event )
    {
        if ( this.storage != null )
        {
            final Event storedEvent = this.storage.store ( event );
            // add to the write queue
            // this.writeQueue.add ( storedEvent );

            // feed the event pools
            for ( final EventListener listener : this.listeners )
            {
                listener.handleEvent ( storedEvent );
            }
        }
        else
        {
            // FIXME: buffer
        }
    }

    public synchronized void addEventListener ( final EventListener listener )
    {
        this.listeners.add ( listener );

        // feed write queue
        for ( final Event event : this.writeQueue )
        {
            listener.handleEvent ( event );
        }
    }

    public synchronized void removeEventListener ( final EventListener listener )
    {
        this.listeners.remove ( listener );
    }

}
