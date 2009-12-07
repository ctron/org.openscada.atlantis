package org.openscada.ae.server.common.event;

import org.openscada.ae.event.EventListener;

public interface EventQuery
{
    public void addListener ( EventListener eventListener );

    public void removeListener ( EventListener eventListener );
}
