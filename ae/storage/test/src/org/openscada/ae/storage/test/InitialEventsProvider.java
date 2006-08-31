package org.openscada.ae.storage.test;

import java.util.Collection;

import org.openscada.ae.core.Event;

public interface InitialEventsProvider
{
    Collection<Event> getInitialEvents ();
}
