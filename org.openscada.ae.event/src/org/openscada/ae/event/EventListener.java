package org.openscada.ae.event;

import org.openscada.ae.Event;

public interface EventListener
{
    public void handleEvent ( Event[] event );
}
