package org.openscada.ae.server.storage;

import java.util.Collection;

import org.openscada.ae.Event;

public interface Query
{
    public boolean hasMore ();

    public Collection<Event> getNext ( long count ) throws Exception;

    public void dispose ();
}
