package org.openscada.ae.storage.common.memory;

import org.openscada.ae.core.Event;

public interface InitialEventsProvider
{
    Event[] getInitialEvents ();
}
