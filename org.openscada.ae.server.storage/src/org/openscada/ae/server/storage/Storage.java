package org.openscada.ae.server.storage;

import org.openscada.ae.Event;

public interface Storage
{
    public Event store ( Event event );

    public Query query ( String filter ) throws Exception;
}
