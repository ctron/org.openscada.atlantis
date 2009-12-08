package org.openscada.ae.server.common.event;

import org.openscada.ae.Event;

public interface EventMatcher
{
    boolean matches(final Event event);
}
