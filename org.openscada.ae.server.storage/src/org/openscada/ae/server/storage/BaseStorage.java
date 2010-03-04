package org.openscada.ae.server.storage;

import java.util.GregorianCalendar;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;

public abstract class BaseStorage implements Storage
{
    private static final boolean allowEntryTimestamp = Boolean.getBoolean ( "org.openscada.ae.server.storage.allowExternalEntryTimestamp" );

    public Event store ( final Event event )
    {
        return store ( event, null );
    }

    protected Event createEvent ( final Event event )
    {
        final EventBuilder builder = Event.create ().event ( event ).id ( UUID.randomUUID () );

        if ( !allowEntryTimestamp || event.getEntryTimestamp () == null )
        {
            // if we are not allowed to have prefilled entryTimestamps
            // or a missing the timestamp anyway
            builder.entryTimestamp ( new GregorianCalendar ().getTime () );
        }

        return builder.build ();
    }

    public Event update ( final UUID id, final String comment ) throws Exception
    {
        return update ( id, comment, null );
    }
}
