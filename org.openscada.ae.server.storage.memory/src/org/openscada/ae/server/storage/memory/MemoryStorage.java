package org.openscada.ae.server.storage.memory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.BaseStorage;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.StoreListener;
import org.openscada.ae.server.storage.memory.internal.EventMatcher.MatchMode;

public class MemoryStorage extends BaseStorage
{

    private List<Event> events = new CopyOnWriteArrayList<Event> ();

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

    public List<Event> getEvents ()
    {
        return Collections.unmodifiableList ( events );
    }
}
