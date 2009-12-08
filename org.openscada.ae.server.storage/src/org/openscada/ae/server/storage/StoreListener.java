package org.openscada.ae.server.storage;

import org.openscada.ae.Event;


public interface StoreListener
{
    public void notify(final Event event);
}
