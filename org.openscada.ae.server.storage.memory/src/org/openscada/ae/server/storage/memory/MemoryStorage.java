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
import org.openscada.ae.server.storage.memory.internal.EventMatcher.MatchMode;

public class MemoryStorage extends BaseStorage
{

    private SortedSet<Event> events = new TreeSet<Event> ();

    private final MatchMode matchMode;

    public MemoryStorage ( MatchMode matchMode )
    {
        this.matchMode = matchMode;
    }

    public Query query ( String filter ) throws Exception
    {
        return new ListQuery ( events, filter, matchMode );
    }

    public Event store ( Event event, StoreListener listener )
    {
        final Event storedEvent = createEvent ( event );
        events.add ( storedEvent );
        listener.notify ( storedEvent );
        return storedEvent;
    }

    public Set<Event> getEvents ()
    {
        return Collections.unmodifiableSet ( events );
    }

    public Event update ( UUID id, String comment, StoreListener listener ) throws Exception
    {
        Event event = null;
        for ( Event found : events )
        {
            if (found.getId ().equals ( event )) {
                event = found;
                break;
            }
        }
        if (event == null) {
            return null;
        }
        events.remove ( event );
        final Event updatedEvent = Event.create ().event ( event ).attribute ( Event.Fields.COMMENT, comment ).build ();
        events.add ( updatedEvent );
        return updatedEvent;
    }
}
