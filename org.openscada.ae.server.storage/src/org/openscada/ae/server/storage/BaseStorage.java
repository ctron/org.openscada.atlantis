package org.openscada.ae.server.storage;

import java.util.GregorianCalendar;
import java.util.UUID;

import org.openscada.ae.Event;

public abstract class BaseStorage implements Storage
{
    public Event store ( final Event event )
    {
        return store ( event, null );
    }

    protected Event createEvent ( final Event event )
    {
        return Event.create ().event ( event ).id ( UUID.randomUUID () ).entryTimestamp ( new GregorianCalendar ().getTime () ).build ();
    }

    public Event update ( final UUID id, final String comment ) throws Exception
    {
        return update ( id, comment, null );
    }
}
