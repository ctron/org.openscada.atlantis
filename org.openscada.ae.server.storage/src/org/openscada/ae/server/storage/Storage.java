package org.openscada.ae.server.storage;

import java.util.UUID;

import org.openscada.ae.Event;

public interface Storage
{
    public Event store ( final Event event );

    public Event store ( final Event event, final StoreListener listener );

    public Query query ( final String filter ) throws Exception;

    public Event update ( final UUID id, final String comment ) throws Exception;

    public Event update ( final UUID id, final String comment, final StoreListener listener ) throws Exception;
}
