package org.openscada.ae.server.storage;

import org.openscada.ae.Event;

public interface Storage
{
    public Event store ( final Event event );

    public Event store ( final Event event, final StoreListener listener );

    public Query query ( final String filter ) throws Exception;
}
