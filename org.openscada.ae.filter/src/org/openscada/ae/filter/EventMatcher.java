package org.openscada.ae.filter;

import org.openscada.ae.Event;

public interface EventMatcher
{
    boolean matches ( final Event event );
}
