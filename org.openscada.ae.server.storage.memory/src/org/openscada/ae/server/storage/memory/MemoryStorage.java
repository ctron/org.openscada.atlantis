package org.openscada.ae.server.storage.memory;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;

public class MemoryStorage extends BaseStorage
{

    private final SortedSet<Event> events = new TreeSet<Event> ();

    public MemoryStorage ()
    {
    }

    public Query query ( final String filter ) throws Exception
    {
        return new ListQuery ( this.events, filter );
    }

    public Event store ( final Event event, final StoreListener listener )
    {
        final Event storedEvent = createEvent ( event );
        this.events.add ( storedEvent );
        listener.notify ( storedEvent );
        return storedEvent;
    }

    public Set<Event> getEvents ()
    {
        return Collections.unmodifiableSet ( this.events );
    }

    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception
    {
        Event event = null;
        for ( Event found : this.events )
        {
            if ( found.getId ().equals ( event ) )
            {
                event = found;
                break;
            }
        }
        if ( event == null )
        {
            return null;
        }
        this.events.remove ( event );
        final Event updatedEvent = Event.create ().event ( event ).attribute ( Event.Fields.COMMENT, comment ).build ();
        this.events.add ( updatedEvent );
        return updatedEvent;
    }
}
