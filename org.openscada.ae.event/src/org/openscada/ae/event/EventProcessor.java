package org.openscada.ae.event;

import java.util.LinkedList;
import java.util.Queue;

import org.openscada.ae.Event;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class EventProcessor
{
    private final SingleServiceTracker tracker;

    private final Filter filter;

    private final Queue<Event> eventQueue = new LinkedList<Event> ();

    private final BundleContext context;

    private EventService service;

    public EventProcessor ( final BundleContext context ) throws InvalidSyntaxException
    {
        this ( "(" + Constants.OBJECTCLASS + "=" + EventService.class.getName () + ")", context );
    }

    public EventProcessor ( final Filter filter, final BundleContext context )
    {
        this.filter = filter;
        this.context = context;
        this.tracker = new SingleServiceTracker ( this.context, this.filter, new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                EventProcessor.this.setService ( (EventService)service );
            }
        } );
    }

    protected synchronized void setService ( final EventService service )
    {
        this.service = service;
        if ( this.service != null )
        {
            publishStoredEvents ( this.service );
        }
    }

    public EventProcessor ( final String filter, final BundleContext context ) throws InvalidSyntaxException
    {
        this ( FrameworkUtil.createFilter ( filter ), context );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    public synchronized void publishEvent ( final Event event )
    {
        final EventService service = this.service;
        if ( service != null )
        {
            service.publishEvent ( event );
        }
        else
        {
            this.eventQueue.add ( event );
        }
    }

    private void publishStoredEvents ( final EventService service )
    {
        Event event = null;
        while ( ( event = this.eventQueue.poll () ) != null )
        {
            service.publishEvent ( event );
        }
    }

}
